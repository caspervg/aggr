package net.caspervg.aggr.worker.core;

import net.caspervg.aggr.worker.core.bean.aggregation.AbstractAggregation;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.read.AggrReader;
import net.caspervg.aggr.worker.core.read.CsvAggrReader;
import net.caspervg.aggr.worker.core.read.JenaAggrReader;
import net.caspervg.aggr.core.AggrCommand;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.util.untyped.UntypedSPARQLRepository;
import net.caspervg.aggr.worker.core.write.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.*;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public abstract class AbstractAggregationExecution implements AggregationExecution {

    /**
     * Builds a Spark context for Java execution
     * @param ac Demands of the user
     * @return Spark Context with some
     */
    protected JavaSparkContext getSparkContext(AggrCommand ac) {
        SparkConf conf = new SparkConf()
                .setAppName("KMeansAggr")
                .setMaster(ac.getSparkMasterUrl());
/*
                .set("spark.eventLog.enabled", "true")
                .set("eventLog.enabled", "true");
*/
        return JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(conf));
    }

    /**
     * Retrieve a suitable {@link AggrReader} based on the user's demands
     *
     * @param ac Demands of the user
     * @param ctx Context of the execution
     * @return Suitable instance of {@link AggrReader} with a pre-set {@link BufferedReader}
     * @throws IOException if the input cannot be opened or read
     */
    protected AggrReader getReader(AggrCommand ac, AggrContext ctx) throws IOException {
        return getReader(ac.getInput(), ac, ctx);
    }

    /**
     * Retrieve a suitable {@link AggrReader} based on the user's demands
     *
     * @param ac Demands of the user
     * @param ctx Context of the execution
     * @return Suitable instance of {@link AggrReader} with a pre-set {@link BufferedReader}
     * @throws IOException if the input cannot be opened or read
     */
    protected AggrReader getReader(String filePath, AggrCommand ac, AggrContext ctx) throws IOException {
        if (filePath.toLowerCase().contains("sparql")) {
            return new JenaAggrReader();
        } else {
            if (!ac.isHdfs()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                return new CsvAggrReader(reader);
            } else {
                Path path = new Path(filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(ctx.getFileSystem().open(path)));
                return new CsvAggrReader(reader);
            }
        }
    }
    /**
     * Retrieve a suitable AggrResultWriter based on the user's demands
     *
     * @param aggrResult Type of the aggregation result
     * @param ac Demands of the user
     * @param ctx Execution context
     * @param <A> Type of the aggregation
     * @param <M> Type of the aggregation result
     * @return Suitable writer, possibly a composite {@link AggrWriter} that selects different channels for different
     * types of output
     */
    protected <A extends AbstractAggregation, M> AggrResultWriter getWriter(
            AggregationResult<A, M> aggrResult,
            AggrCommand ac,
            AggrContext ctx) {
        AggrWriter metaWriter = new Rdf4jAggrWriter(new UntypedSPARQLRepository(ac.getService()), ac.isWriteProvenance());
        AggrWriter dataWriter;

        if (ac.isWriteDataCsv()) {
            String dataPath;

            try {
                String hdfsUrl = ac.getHdfsUrl();
                String dirPath = ac.getOutput();
                String fileName = aggrResult.getAggregation().getUuid() + ".csv";

                if (ac.isSpark()) {
                    if (StringUtils.isNotBlank(hdfsUrl)) {
                        Path parent = new Path(dirPath);
                        Path child = new Path(parent, fileName);
                        FSDataOutputStream os = ctx.getFileSystem().create(child, false);
                        dataWriter = new CsvAggrWriter(new PrintWriter(os));
                        dataPath = ac.getHdfsUrl() + dirPath + "/" + fileName;
                    } else {
                        dataWriter = new CsvAggrWriter(new PrintWriter(new File(dirPath + "/" + fileName)));
                        dataPath = "file://" + dirPath + "/" + fileName;
                    }
                } else {
                    dataWriter = new CsvAggrWriter(new PrintWriter(new File(dirPath + "/" + fileName)));
                    dataPath = "file://" + dirPath + "/" + fileName;
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            return new CompositeAggrWriter(dataWriter, metaWriter, ac.isWriteProvenance(), dataPath);     // Split data to CSV, metadata to triple store
        }

        return new CompositeAggrWriter(metaWriter, metaWriter, ac.isWriteProvenance(), ac.getService());  // Write data and metadata to the triple store
    }

    /**
     * Stop the Spark Context if it exists
     * @param context Context of the execution
     */
    protected void stop(AggrContext context) {
        if (context.getSparkContext() != null) {
            context.getSparkContext().stop();
        }
    }

    protected AggrContext createContext(Map<String, String> parameters, AggrCommand ac) throws URISyntaxException, IOException {
        AggrContext.Builder acb = AggrContext.builder()
                .parameters(parameters)
                .inputClass(ac.getInputClass())
                .outputClass(ac.getOutputClass());
        if (ac.isSpark()) {
            String hdfsUrl = ac.getHdfsUrl();
            JavaSparkContext sparkCtx = getSparkContext(ac);

            acb.sparkContext(sparkCtx);
            if (StringUtils.isNotBlank(hdfsUrl)) {
                FileSystem hdfs = FileSystem.get(new URI(hdfsUrl), sparkCtx.hadoopConfiguration());
                acb.fileSystem(hdfs);
            }
        }

        return acb.build();
    }

}
