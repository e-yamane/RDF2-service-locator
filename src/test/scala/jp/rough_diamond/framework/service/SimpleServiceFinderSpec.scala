package jp.rough_diamond.framework.service

import org.scalatest.Spec
import jp.rough_diamond.commons.di.DIContainerFactory
import jp.rough_diamond.commons.di.AbstractDIContainer

class SimpleServiceFinderSpec extends Spec {
	lazy val di = new AbstractDIContainer {
	    def getObject[T](cl:Class[T], key:Object) : T =  {
	        if(key.asInstanceOf[String].endsWith("SampleService1")) {
	            target.asInstanceOf[T]
	        } else {
	        	null.asInstanceOf[T]
	        }
	    }
	    def getSource[T](cl:Class[T]) : T = null.asInstanceOf[T]
	}
	

	describe("SimpleServiceFinderの振る舞い") {
	  val finder = new SimpleServiceFinder
	  it("DIコンテナに存在しない場合はデフォルトのインスタンスが返却される事") {
	      classOf[DIContainerFactory].synchronized  {
    	      DIContainerFactory.setDIContainer(di)
	          val service = finder.getService(classOf[SampleService2], classOf[SampleService3])
	          assert(service.getClass == classOf[SampleService3])
	      }
	  }
	  it("DIコンテナに存在する場合はDIコンテナで返却されたインスタンスが返却される事") {
	      classOf[DIContainerFactory].synchronized  {
    	      DIContainerFactory.setDIContainer(di)
	          val service = finder.getService(classOf[SampleService1], classOf[SampleService1])
	          assert(target eq service)
	      }
	  }
	}
	
    lazy val target = new SampleService2
}

class SampleService1 extends Service
class SampleService2 extends SampleService1
class SampleService3 extends SampleService2
