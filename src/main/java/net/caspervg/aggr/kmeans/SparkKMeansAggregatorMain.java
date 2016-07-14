package net.caspervg.aggr.kmeans;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.read.CsvAggrReader;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.write.AggrWriter;
import net.caspervg.aggr.core.write.Rdf4jAggrWriter;
import net.caspervg.aggr.grid.SparkGridAggregatorMain;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.util.HashMap;
import java.util.Map;

public class SparkKMeansAggregatorMain {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("KMeansSpark").setMaster("local[4]");
        JavaSparkContext sparkCtx = new JavaSparkContext(conf);
        Map<String, String> params = new HashMap<>();
        params.put("input_path", SparkGridAggregatorMain.class.getResource("/measurements2.csv").getPath());
        params.put("num_centroids", "2");
        AggrContext ctx = new AggrContext(
                params,
                sparkCtx
        );

        Dataset dataset = new Dataset("test_dataset1");
        Iterable<Measurement> meas = new CsvAggrReader().read(ctx);
        Iterable<AggregationResult<KMeansAggregation, Centroid>> results = new SparkKMeansAggregator().aggregate(dataset, meas, ctx);

        AggrWriter writer = new Rdf4jAggrWriter(new SPARQLRepository("http://localhost:8890/sparql"));
        for (AggregationResult<KMeansAggregation, Centroid> res : results) {
            KMeansAggregation gag = res.getAggregation();
            Iterable<Centroid> rescents = res.getResults();

            writer.writeAggregation(gag, ctx);
            rescents.forEach((centroid) -> writer.writeCentroid(centroid, ctx));
        }

        writer.writeDataset(dataset, ctx);

    }
}
