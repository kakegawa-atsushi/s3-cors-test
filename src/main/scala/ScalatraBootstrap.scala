import jp.classmethod.s3corstest._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new RootController, "/*")
    context.mount(new SignController, "/sign/*")
  }
}
