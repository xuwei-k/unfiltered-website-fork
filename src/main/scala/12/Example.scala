package com.example

import unfiltered.request._
import unfiltered.response._

/** unfiltered plan */
class App extends unfiltered.filter.Plan {
  import QParams._

  def intent = {
    case GET(Path(p)) => 
      Ok ~> view(Map.empty)(<p> What say you? </p>)
    case POST(Path(p) & Params(params)) =>
      val vw = view(params)_
      val expected = for { 
        int <- lookup("int") is
          int { _ + " is not an integer" } is
          required("missing int")
        word <- lookup("palindrome") is
          trimmed is 
          nonempty("Palindrome is empty") is
          pred(palindrome, { _ + " is not a palindrome" }) is
          required("missing palindrome")
      } yield vw(<p>Yup. { int.get } is an integer and { word.get } is a palindrome. </p>)
      expected(params) orFail { fails =>
        BadRequest ~> vw(<ul> { fails.map { f => <li>{f.error} </li> } } </ul>)
      }
  }
  def palindrome(s: String) = s.toLowerCase.reverse == s.toLowerCase
  def view(params: Map[String, Seq[String]])(body: scala.xml.NodeSeq) = {
    def p(k: String) = params.get(k).flatMap { _.headOption } getOrElse("")
    Html(
     <html><body>
       { body }
       <form method="POST">
         Integer <input name="int" value={ p("int") } ></input>
         Palindrome <input name="palindrome" value={ p("palindrome") } />
         <input type="submit" />
       </form>
     </body></html>
   )
  }
}

/** embedded server */
object Server {
  def main(args: Array[String]) {
    unfiltered.jetty.Http.local(8080).filter(new App).run
  }
}
