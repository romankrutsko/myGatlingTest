
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object RandomGenerator {
  def randomString(length: Int): String = {
    val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    val salt = new StringBuilder
    val rnd = new scala.util.Random
    while (salt.length < length) { // length of the random string.
      val index = (rnd.nextFloat() * SALTCHARS.length).asInstanceOf[Int]
      salt.append(SALTCHARS.charAt(index))
    }
    val saltStr = salt.toString
    saltStr
  }
  def randomCoverPhotosRequest(): String =
    """{"idBook" : 1,"url":"""".stripMargin + RandomGenerator.randomString(25) +""""
       |}""".stripMargin
}
class Usersimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://fakerestapi.azurewebsites.net/")


  val post = scenario("Post CoverPhotos")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomCoverPhotosRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post CoverPhotos")
        .post("/api/v1/CoverPhotos/")
        .body(StringBody("${postrequest}")).asJson
    )

  val get = scenario("Get CoverPhotos")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomCoverPhotosRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post CoverPhotos")
        .post("/api/v1/CoverPhotos/")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("CoverPhotosId"))
    )
    .exitHereIfFailed
    .exec(
      http("Get CoverPhotos")
        .get("/api/v1/CoverPhotos/5")
    )

  val put = scenario("Put CoverPhotos")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomCoverPhotosRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post CoverPhotos")
        .post("/api/v1/CoverPhotos/")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("CoverPhotosId"))
    )
    .exitHereIfFailed
    .exec(sessionPut => {
      val sessionPutUpdate = sessionPut.set("putrequest", RandomGenerator.randomCoverPhotosRequest())
      sessionPutUpdate
    })
    .exec(
      http("Put CoverPhotos")
        .put("/api/v1/CoverPhotos/${CoverPhotosId}")
        .body(StringBody("${putrequest}")).asJson
    )

  val delete = scenario("Delete CoverPhotos")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomCoverPhotosRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post CoverPhotos")
        .post("/api/v1/CoverPhotos")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("CoverPhotosId"))
    )
    .exitHereIfFailed
    .exec(sessionPut => {
      val sessionPutUpdate = sessionPut.set("putrequest", RandomGenerator.randomCoverPhotosRequest())
      sessionPutUpdate
    })
    .exec(
      http("Delete CoverPhotos")

        .delete("/api/v1/CoverPhotos/${CoverPhotosId}")
    )

  setUp(post.inject(rampUsers(20).during(5.seconds)).protocols(httpProtocol),
    get.inject(rampUsers(20).during(5.seconds)).protocols(httpProtocol),
    put.inject(rampUsers(25).during(5.seconds)).protocols(httpProtocol),
    delete.inject(rampUsers(30).during(5.seconds)).protocols(httpProtocol))

}
