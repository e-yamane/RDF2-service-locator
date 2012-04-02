package jp.rough_diamond.framework.service

import org.scalatest.Spec
import jp.rough_diamond.commons.di.AbstractDIContainer
import jp.rough_diamond.commons.di.DIContainerFactory

class ServiceLocatorSpec extends Spec {
    describe("DIコンテナにServiceFinderの定義が無い") {
	  it("デフォルトのServiceFinderが用いられる事") (pending)
	}

	describe("DIコンテナにServiceFinderの定義がある場合") {
	  it("指定されたサービスインスタンスが取得できる事") {
	      classOf[DIContainerFactory].synchronized  {
	          DIContainerFactory.setDIContainer(new DIContainerImpl);
	          val service = ServiceLocator.getService(classOf[ServiceLocatorSpecService1]);
	          assert(service.getClass eq classOf[ServiceLocatorSpecService1]);
	      }
	  }
	  it("ちゃんとキャッシュされていること") {
	      classOf[DIContainerFactory].synchronized  {
	          DIContainerFactory.setDIContainer(new DIContainerImpl);
	          val service1 = ServiceLocator.getService(classOf[ServiceLocatorSpecService2]);
	          val service2 = ServiceLocator.getService(classOf[ServiceLocatorSpecService2]);
	          assert(service1 eq service2);
	      }
	  }
	}
	
	class DIContainerImpl extends AbstractDIContainer {
	    def getObject[T](cl:Class[T], key:Object) : T =  {
	        if(key.asInstanceOf[String].equals(ServiceLocator.SERVICE_FINDER_KEY)) {
	            finder.asInstanceOf[T]
	        } else {
	        	null.asInstanceOf[T]
	        }
	    }
	    def getSource[T](cl:Class[T]) : T = null.asInstanceOf[T]
	}
	
	lazy val finder = new SimpleServiceFinder
}

class ServiceLocatorSpecService1 extends Service
class ServiceLocatorSpecService2 extends Service
