This project should help you get started with ScalaQuery <http://scalaquery.org>.

You only need a JRE and sbt 0.7 <http://code.google.com/p/simple-build-tool/> installed.

1. Clone this project (or download it manually from
<https://github.com/szeiger/scalaquery-examples/archives/master>):

> git clone git://github.com/szeiger/scalaquery-examples.git

2. Run it with sbt:

> cd scalaquery-examples
> sbt update
> sbt run

All examples can be run as normal applications. "sbt run" will give you a menu
of all available examples:

* FirstExample: Start with this if you are new to ScalaQuery. It creates two
  tables, inserts some data and performs a number of queries.

* MultiDBExample: Shows how to write portable code based on a ScalaQuery
  profile and run it against different DBMSs.

* UseInvoker: Shows various ways of reading data from an Invoker.

* CallNativeDBFunction: Shows how to use a database function which is not
  provided by the ScalaQuery driver.
