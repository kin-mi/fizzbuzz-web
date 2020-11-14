package backend.service

import backend.model.{FizzBuzzResult, FizzbuzzResponse}
import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}
import com.google.gson.Gson
import java.net._
import scala.jdk.javaapi.OptionConverters.toScala

class FizzBuzz extends HttpFunction {

  /** Gson instance */
  val gson: Gson = new Gson

  override def service(httpRequest: HttpRequest,
    httpResponse: HttpResponse): Unit = {
    val paramStart = toScala(httpRequest.getFirstQueryParameter("start"))
    val paramRange =
      toScala(httpRequest.getFirstQueryParameter("range")).getOrElse("1")

    val isNotAllowMethod = !"GET".equals(httpRequest.getMethod)
    val isInvalidArgument = paramStart.isEmpty || paramStart.get.toIntOption.isEmpty || paramRange.toIntOption.isEmpty
    if (isNotAllowMethod) {
      httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD,
        ErrMsgNotAllowMethod)
    } else if (isInvalidArgument) {
      httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
        ErrMsgInvalidArgument)
    } else {
      val startNumber = paramStart.get.toInt
      val range = paramRange.toInt
      val data = (startNumber to range)
        .map(i => {
          FizzBuzzResult(i, fizzbuzz(i))
        })
        .toArray

      httpResponse.setStatusCode(HttpURLConnection.HTTP_OK)
      httpResponse.getWriter.write(
        gson.toJson(FizzbuzzResponse(success = true, data = data))
      )
    }
  }

  def fizzbuzz(i: Int): String = (i % 3, i % 5) match {
    case (0, 0) => "FizzBuzz"
    case (0, _) => "Fizz"
    case (_, 0) => "Buzz"
    case _      => i.toString
  }
}
