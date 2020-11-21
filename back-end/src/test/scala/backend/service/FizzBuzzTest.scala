package backend.service

import backend.UnitSpec
import com.google.cloud.functions.{HttpRequest, HttpResponse}
import java.io.{BufferedWriter, StringWriter}
import java.net.HttpURLConnection
import java.util.Optional

import backend.model.FizzbuzzResponse
import com.google.gson.Gson
import org.mockito.Mockito.{clearInvocations, verify, when}
import org.scalatestplus.mockito.MockitoSugar.mock

class FizzBuzzTest extends UnitSpec {

  /** Gson instance */
  val gson: Gson = new Gson

  /** HTTP Mock */
  val mockHttpRequest: HttpRequest = mock[HttpRequest]
  val mockHttpResponse: HttpResponse = mock[HttpResponse]

  var writerOut: BufferedWriter = _
  var responseOut: StringWriter = _

  override def beforeEach(): Unit = {
    responseOut = new StringWriter
    writerOut = new BufferedWriter(responseOut)
    when(mockHttpResponse.getWriter).thenReturn(writerOut)
    super.beforeEach()
  }

  override def afterEach(): Unit = {
    super.afterEach()
    clearInvocations(mockHttpRequest, mockHttpResponse)
  }

  def getResponse: FizzbuzzResponse = {
    writerOut.flush()
    gson.fromJson(responseOut.toString, classOf[FizzbuzzResponse])
  }

  "Normal Request" should "Fizz" in {
    val startNumber = "3"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == 1)
    assert(response.data(0).fizzbuzz == "Fizz")
  }

  it should "Buzz" in {
    val startNumber = "5"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == 1)
    assert(response.data(0).fizzbuzz == "Buzz")
  }

  it should "FizzBuzz" in {
    val startNumber = "15"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == 1)
    assert(response.data(0).fizzbuzz == "FizzBuzz")
  }

  it should "Maximum Start Number" in {
    val startNumber = "2147483647"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == 1)
    assert(response.data(0).fizzbuzz == startNumber)
  }

  it should "Minimum Start Number" in {
    val startNumber = "1"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == 1)
    assert(response.data(0).fizzbuzz == startNumber)
  }

  it should "Maximum Range" in {
    val startNumber = "1"
    val rangeNumber = "1000"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))
    when(mockHttpRequest.getFirstQueryParameter(ParamRange))
      .thenReturn(Optional.of(rangeNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == rangeNumber.toInt)
  }

  it should "Minimum Range" in {
    val startNumber = "1"
    val rangeNumber = "1"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))
    when(mockHttpRequest.getFirstQueryParameter(ParamRange))
      .thenReturn(Optional.of(rangeNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == rangeNumber.toInt)
  }

  it should "FizzBuzz List" in {
    val startNumber = "1"
    val range = "15"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))
    when(mockHttpRequest.getFirstQueryParameter(ParamRange))
      .thenReturn(Optional.of(range))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).getWriter
    val response = getResponse
    assert(response.data.length == 15)
    val fizzbuzzList = Array("1",
      "2",
      "Fizz",
      "4",
      "Buzz",
      "Fizz",
      "7",
      "8",
      "Fizz",
      "Buzz",
      "11",
      "Fizz",
      "13",
      "14",
      "FizzBuzz")
    fizzbuzzList.zipWithIndex.foreach {
      case (e, i) =>
        assert(response.data(i).fizzbuzz == e)
    }
  }

  "Abnormal Request" should "405 Method not allowed." in {
    val startNumber = "1"
    when(mockHttpRequest.getMethod).thenReturn("POST")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_METHOD,
      ErrMsgNotAllowMethod)
  }

  it should "400 Invalid argument." in {
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.empty)

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
      ErrMsgInvalidArgument)
  }
  it should "400 Invalid start number.#over" in {
    val startNumber = "2147483648"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
      ErrMsgInvalidStartNumber)
  }

  it should "400 Invalid start number.#under" in {
    val startNumber = "0"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
      ErrMsgInvalidStartNumber)
  }

  it should "400 Invalid start number.#character" in {
    val startNumber = "a"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
      ErrMsgInvalidStartNumber)
  }

  it should "Invalid range number.#over" in {
    val startNumber = "1"
    val range = "1001"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))
    when(mockHttpRequest.getFirstQueryParameter(ParamRange))
      .thenReturn(Optional.of(range))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
      ErrMsgInvalidRangeNumber)
  }

  it should "Invalid range number.#under" in {
    val startNumber = "1"
    val range = "0"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))
    when(mockHttpRequest.getFirstQueryParameter(ParamRange))
      .thenReturn(Optional.of(range))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
      ErrMsgInvalidRangeNumber)
  }

  it should "Invalid range number.#character" in {
    val startNumber = "1"
    val range = "a"
    when(mockHttpRequest.getMethod).thenReturn("GET")
    when(mockHttpRequest.getFirstQueryParameter(ParamStart))
      .thenReturn(Optional.of(startNumber))
    when(mockHttpRequest.getFirstQueryParameter(ParamRange))
      .thenReturn(Optional.of(range))

    new FizzBuzz().service(mockHttpRequest, mockHttpResponse)

    verify(mockHttpResponse).setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST,
      ErrMsgInvalidRangeNumber)
  }
}
