import unfiltered.mac._
import unfiltered.request._
import unfiltered.filter._
import unfiltered.Cycle.Intent

object example_mac {

val algorithm: String = ???
def tokenSecret(id: String): Option[String] = ???

new unfiltered.filter.Plan{

// #example1
def intent: Intent[Any, Any] = {
  case MacAuthorization(id, nonce, bodyhash, ext, mac) & req =>
    tokenSecret(id) match {
      case Some(key) =>
        // compare a signed request with the signature provided
        Mac.sign(req, nonce, ext, bodyhash, key, algorithm).fold({ err =>
          // error signing request...
          ???
        }, { sig =>
          if(sig == mac) ??? // request is trust worthy...
          else           ??? // request is untrusted...
        })
      case _ => // could not find token for the provided id
        ???
    }
  }
}
// #example1

}
