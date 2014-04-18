# zhihugrabber

A Clojure program to grab articles from zhuanlan.zhihu.com and send to your Kindle device.

## Usage

### Build
Clone this project and build it by `lein uberjar`.

### Configuration
1. Copy `zhihugrabber.edn` to the same folder of the jar you built.
2. Modify it as your need.
  1. `:sources` is a set ofg zhuanlan names.
  2. `:latest` is a map of from which datetime to grab each zhuanlan. Will be updated after every run.
  3. `:mail` is email settings. Will send via local server if `:smtp-server` absents.

## License

Copyright © 2014 Crane Jin

Distributed under the Eclipse Public License either version 1.0 or any later version.

