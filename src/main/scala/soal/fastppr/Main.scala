package soal.fastppr
import com.twitter.cassovary.graph.{StoredGraphDir, TestGraphs}
import com.twitter.cassovary.util.{NodeNumberer, ParseString}
import spray.json._
import scala.collection.mutable.Map
import scala.collection.concurrent.TrieMap
import DefaultJsonProtocol._
import java.io.PrintWriter
import scala.collection.mutable
import scala.util.Random
import com.twitter.cassovary.util.io.{AdjacencyListGraphReader, LabelsReader}
import scala.collection.parallel.ForkJoinTaskSupport
import java.util.concurrent.atomic.AtomicInteger

object Main{
  def main(args:Array[String] ):Unit={
//    val pool: ExecutorService = Executors.newFixedThreadPool(8)
    val dir = "/home/leon/scala/graphs"
    val nodeNumberer: NodeNumberer[Int] = new NodeNumberer.IntIdentity()
    val graph = AdjacencyListGraphReader.forIntIds(dir, "parallel",nodeNumberer,  graphDir=StoredGraphDir.BothInOut).toArrayBasedDirectedGraph()
    val config = FastPPRConfiguration.defaultConfiguration
    //val test_n = graph.getNodeById(0).get
    //println(graph.isBiDirectional)
//    val v = graph.getNodeById(0).get
//    println(v.outboundCount)
//    println(v.inboundCount)
    //sample = Random.shuffle(list).take(n)
    val nodes_ids = (1 to graph.nodeCount).toList
    val sample_size = 1251
    val sample = Random.shuffle(nodes_ids).take(sample_size).par // parralel sample
    sample.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(8))


    val ResultMap:TrieMap[String, Float] = TrieMap()
//    println(FastPPR.estimatePPR(graph, 0, 5 , config))
    val counter = new AtomicInteger(0)
    sample.foreach { case f =>
      for (t <- 0 to graph.nodeCount - 1) {
                try {
                  val ppr = FastPPR.estimatePPR(graph, f, t, config)
                  val key = f.toString() + "#" + t.toString()
                  ResultMap(key) = ppr
                }
                catch {
                  case: Exception  => {
                    println("exception for the nodes " + f.toString()+ " and "+t.toString())
                    ResultMap(key) = 0
                }
              }
      val c = counter.incrementAndGet();
      println(c.toString()+"/1251")
    }

//    for( f<- sample) {
//      for (t <- 0 to graph.nodeCount - 1) {
//        val ppr = FastPPR.estimatePPR(graph, f, t, config)
//        val key = f.toString()+"#"+t.toString()
//        ResultMap(key) = ppr
//      }
//    }

    val js = ResultMap.toMap.toJson.compactPrint
    new PrintWriter("results_full.json") { write(js); close }
    //    for (n <- 0 to 100)
//      for (t<-0 to 100)
//      if(FastPPR.estimatePPR(graph, t, n , config)!=0 && t!=n){
//        println(FastPPR.estimatePPR(graph, t, n , config), n)}
  }
}