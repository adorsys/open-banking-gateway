docker run -it --rm -v "$PWD":/src -w /src -u "$(id -u "${USER}"):$(id -g "${USER}")" --env TRAVIS_TAG g0lden/mkdocs make site
