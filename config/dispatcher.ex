defmodule Dispatcher do

  use Plug.Router

  def start(_argv) do
    port = 80
    IO.puts "Starting Plug with Cowboy on port #{port}"
    Plug.Adapters.Cowboy.http __MODULE__, [], port: port
    :timer.sleep(:infinity)
  end

  plug Plug.Logger
  plug :match
  plug :dispatch

  match "/pipelines/*path" do
    Proxy.forward conn, path, "http://pipeline/pipelines/"
  end

  match "/steps/*path" do
    Proxy.forward conn, path, "http://pipeline/steps/"
  end

  match "/init-daemon/*path" do
    Proxy.forward conn, path, "http://initDaemon/"
  end

  match "/request/*path" do
    Proxy.forward conn, path, "http://aggr_web/aggregations/"
  end

  match "/datasets/*path" do
    Proxy.forward conn, path, "http://resource/datasets/"
  end

  match "/aggregations/*path" do
    Proxy.forward conn, path, "http://resource/aggregations/"
  end

  match "/measurements/*path" do
    Proxy.forward conn, path, "http://cache/measurements/"
  end

  match _ do
    send_resp( conn, 404, "Route not found" )
  end

end
