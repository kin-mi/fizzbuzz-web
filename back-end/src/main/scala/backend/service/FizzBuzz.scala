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
    val paramStart = toScala(httpRequest.getFirstQueryParameter(ParamStart))
    val paramRange =
      toScala(httpRequest.getFirstQueryParameter(ParamRange)).getOrElse("1")

    val isNotAllowMethod = !"GET".equals(httpRequest.getMethod)
    val isInvalidArgument = paramStart.isEmpty

    def isInvalidStartNumber: Boolean = {
      val num = paramStart.get
      num.toIntOption.isEmpty ||
      num.toInt < 1 ||
      num.toInt > BigInt(
        Integer.MAX_VALUE - paramRange.toIntOption.getOrElse(1)) + 1
    }

    def isInvalidRangeNumber: Boolean = {
      val num = paramRange
      num.toIntOption.isEmpty || num.toInt < 1 || num.toInt > RangeMax
    }

    if (isNotAllowMethod) {
      httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD,
                                 ErrMsgNotAllowMethod)
    } else if (isInvalidArgument) {
      httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
                                 ErrMsgInvalidArgument)
    } else if (isInvalidStartNumber) {
      httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
                                 ErrMsgInvalidStartNumber)
    } else if (isInvalidRangeNumber) {
      httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
                                 ErrMsgInvalidRangeNumber)
    } else {
      val startNumber = BigInt(paramStart.get.toInt)
      val range = BigInt(paramRange.toInt)
      val data = (startNumber until startNumber + range)
        .map(_.toInt)
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
