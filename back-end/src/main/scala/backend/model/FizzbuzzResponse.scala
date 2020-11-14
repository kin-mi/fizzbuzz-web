package backend.model

case class FizzbuzzResponse(
  success: Boolean = false,
  data: Array[FizzBuzzResult] = null
)
