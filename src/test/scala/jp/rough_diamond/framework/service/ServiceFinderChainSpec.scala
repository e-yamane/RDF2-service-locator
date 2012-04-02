package jp.rough_diamond.framework.service
import org.scalatest.Spec
import scala.collection.JavaConverters._

class ServiceFinderChainSpec extends Spec {
  lazy val finder = new ServiceFinderChain(finders.asJava)
  
  describe("最初のFinderにしかないサービスタイプを指定") {
    it("最初のFinderからの返却値が返却される事") {
        val service = finder.getService(classOf[SampleService1], classOf[SampleService1])
        assert(service eq service1_1)
    }
  }
  
  describe("２つ目のFinderにしかないサービスタイプを指定") {
    it("２つ目のFinderからの返却値が返却される事") {
        val service = finder.getService(classOf[SampleService2], classOf[SampleService2])
        assert(service eq service2_2)
    }
  }

  describe("どちらのFinderにもあるサービスタイプを指定") {
    it("１つ目のFinderからの返却値が返却される事") {
        val service = finder.getService(classOf[SampleService3], classOf[SampleService3])
        assert(service eq service3_1)
    }
  }

  lazy val finders = Seq[ServiceFinder](
      new ServiceFinder {
    	  def getService[T <: Service](cl:Class[T], defaultClass:Class[_ <: T]) : T = {
    	      val name = cl.getSimpleName
              name match {
                  case "SampleService1" => service1_1.asInstanceOf[T]
                  case "SampleService3" => service3_1.asInstanceOf[T]
                  case other => null.asInstanceOf[T]
              }
    	  }
      }
      ,new ServiceFinder {
    	  def getService[T <: Service](cl:Class[T], defaultClass:Class[_ <: T]) : T = {
    	      val name = cl.getSimpleName
              name match {
                  case "SampleService2" => service2_2.asInstanceOf[T]
                  case "SampleService3" => service3_2.asInstanceOf[T]
                  case other => null.asInstanceOf[T]
              }
    	  }
      }
  )
  
  class SampleService1 extends Service
  class SampleService2 extends Service
  class SampleService3 extends Service
  class SampleService4 extends Service

  lazy val service1_1 = new SampleService1
  lazy val service1_2 = new SampleService1
  lazy val service2_1 = new SampleService2
  lazy val service2_2 = new SampleService2
  lazy val service3_1 = new SampleService3
  lazy val service3_2 = new SampleService3
}