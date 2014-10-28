import org.gedcom4j.parser.GedcomParser

object GenParser {

  def loadFile(filename : String) {
    val gp = new GedcomParser()
    gp.load(filename)

    // TODO
  }

  def main(args : Array[String]) {

    if (args.size == 0) {
      println("A GEDCOM file is required.")
      System.exit(-1)
    }

    args.foreach(loadFile)
  }

}
