This project should help you get started with SLICK <https://github.com/slick/slick>.

You only need a JRE and sbt 0.12.0 <https://github.com/harrah/xsbt/wiki> installed.

1. Clone this project (or download it manually from
<https://github.com/slick/slick-examples/archives/master>):

> git clone git://github.com/slick/slick-examples.git

2. Run it with sbt:

> cd slick-examples
> sbt update
> sbt run

All examples can be run as normal applications. "sbt run" will give you a menu
of all available examples:

* FirstExample: Start with this if you are new to SLICK. It uses the SLICK
  Query Language to create two tables, insert some data and perform a number
  of queries.

* MultiDBExample: Shows how to write portable code based on a SLICK profile
  and run it against different DBMSs.

* UseInvoker: Shows various ways of reading data from an Invoker.

* CallNativeDBFunction: Shows how to use a database function which is not
  provided by the SLICK driver in the Query Language.
