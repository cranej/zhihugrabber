# zhihugrabber

A Clojure program to grab articles from zhuanlan.zhihu.com and send to your Kindle device.

## Usage

### Download compiled jar or Build it yourself

#### Use precompiled jar ####
1. Just download it from [Releases][]
2. To run it, install Java Runtime and `java -jar zhihugrabber.jar`.

#### Build ####
1. JDK and leinigen required.
2. Clone this project and build it by `lein uberjar`.

[Releases]: https://github.com/cranej/zhihugrabber/releases


### Configuration
1. Copy `zhihugrabber.edn` to the same folder of the jar you built.
2. Modify it as your need.
  1. `:sources` is a set of column names. For example, if you want to grab from http<nolink>://zhuanlan.zhihu.com/**xiliutang** , add **:xiliutang** in the `:sources`. 
  2. `:latest` is a map of from which datetime to grab each column. Will be updated after every run.
  3. `:mail` is email settings. Please makesure the email address you configured here has been added to your Kindle approved email list.  Will send via local server if `:smtp-server` absents.

### TODO ###
1. Do real incrementally grabbing. Currently it just grab latest 100 articles and then filter by last grabbed date.
2. Use `pandoc` and `ebook-convert` to convert grabbed HTML to MOBI format. In this way, images in articles can be grabbed correctly.

## License

Copyright © 2014 Crane Jin

Distributed under the Eclipse Public License either version 1.0 or any later version.

