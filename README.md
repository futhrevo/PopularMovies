# Popular Movies

Android app made from scratch as part of Udacity Android Developer Nanodegree program.
This app uses optimized layouts for both phone and tablet

## Features

* Discover movies in a grid of movie posters, allows users to change sort order based on rating, favorites and popular
* View and play trailers ( either in the youtube app or a web browser)
* Read reviews of a selected movie
* Users can mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request*.

## Prerequisites

This app uses [The Movie Database](https://www.themoviedb.org/documentation/api) API to retrieve movies.
You must provide your own API key in order to build the app. When you get it, just paste it to:
    ```
    app/src/main/res/values/api-keys.xml
    ```

## Screenshots

![screen](../master/art/phone-min.png)

![screen](../master/art/phone-detail.png)

![screen](../master/art/phone-settings.png)

![screen](../master/art/tablet-land-min.png)

## Libraries

* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Picasso](https://github.com/square/picasso)
* [LinearListView](https://github.com/frankiesardo/LinearListView)
* [Stetho](https://github.com/facebook/stetho)

## License

    Copyright 2016 Rakesh Kalyankar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.