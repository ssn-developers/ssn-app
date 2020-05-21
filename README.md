![Cover Image](ssn_app_cover.png)

<h1 align="center">
  SSNCE
</h1>

<p align="center">
  Join our active, engaged community: <br>
  <strong>
    <a href="https://play.google.com/store/apps/details?id=in.edu.ssn.ssnapp">Playstore</a>
    |
    <a href="https://discord.gg/UZCM4Ae">Discord</a>
    |
    <a href="https://www.facebook.com/ssnceapp">Facebook</a>
    |
    <a href="https://www.instagram.com/ssnce_app">Instagram</a>
  </strong>
</p>

<p align="center">
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-green"></a>
    <a href="https://github.com/ssn-developers/ssn-app/pulls"><img alt="PRs Welcome" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square"></a>
  <a href="https://github.com/ssn-developers/ssn-app/issues"><img alt="Issues" src="https://img.shields.io/github/issues-raw/ssn-developers/ssn-app?style=flat-square"></a>
    <a href="https://github.com/ddlogesh/zinger-framework/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement"><img alt="Enhancement Welcome" src="https://img.shields.io/badge/enhancement-welcome-green.svg?style=flat-square"></a>
</p>

SSNCE App allows you to get timely updates on feeds, events, workshops, clubs and placements from respective departments &amp; exam cell.

## Installation Setup

#### Fork Project

* [Fork](https://github.com/ssn-developers/ssn-app/fork) the Main Repository.

* Clone the forked repository `git clone forked_repo_url`

* Import the cloned project into Android Studio.

#### Setup Firebase

* Create a firebase project and [add](https://firebase.google.com/docs/android/setup) your android app to that project.

* Enable Google Sign-In in the Firebase console:
    * On the **Authentication** section, choose the **Sign-in method** tab and enable the Google sign-in method.
    
* Add your app's SHA-1 fingerprint:
    * [Generate](https://stackoverflow.com/a/36257777/10489902) SHA-1 key in android studio.
    * Go to **Settings** page of the Firebase console and add your SHA-1 key under **SHA certificate fingerprints**.
    
* Enable [Cloud Firestore](https://firebase.google.com/docs/firestore/quickstart#create) database and [Storage](https://firebase.google.com/docs/storage/android/start#create-default-bucket) in your firebase project.

* Populate mock data:
    * Install firebase-admin SDK
        ```
        pip3 install --upgrade setuptools
        pip3 install --upgrade gcloud
        pip3 install --upgrade firebase-admin
        ```
    * In the Firebase console, open **Settings > Service Accounts** and click **Generate New Private Key**
    * Open the python script [mockdata.py](https://github.com/ssn-developers/ssn-app/blob/master/mockdata.py).
    * Replace the value of the **JSON_PATH** variable with the downloaded JSON file path in the script.
    * Run the python script now `python3 mockdata.py`
    * Run the android project in an emulator or a physical device.

#### Note

* You may receive an error in the Logcat stating **Composite Index** not found. Please follow the [instruction](https://firebase.google.com/docs/firestore/query-data/indexing?authuser=0#create_a_missing_index_through_an_error_message) to create composite index manually.
* To enable notification in global chat, generate a new API Key from the [API console](https://console.developers.google.com/) and replace the generated key with the **SERVER_KEY** variable in [Constants](https://github.com/ssn-developers/ssn-app/blob/master/app/src/main/java/in/edu/ssn/ssnapp/utils/Constants.java) file.

## Enhancement

![Cover Image](ssn_app_cover.png)

#### Project corner

Project corner is one of the highly recommended features by the ssnites for showcasing their skills and talents. It should help the students and alumni of SSN to share their project works, awards, etc to their peers.

#### Bus tracking

Tired of calling your bus mates in verifying whether the bus has arrived? Develop the bus tracking feature, to save the trouble of day scholars in tracking the location of the buses in realtime using Google Maps.

#### Attendance management

Worried about the attendance percentage at the end of every semester, then this feature is for you. Help your bunkmates to track and manage their attendance with ease.

## Contribution Guidelines

#### Git commit messages
To speed up the review process and to keep the logs tidy, we recommend the following simple rules on how to write good commit messages:

##### Summary line
* It should contain less than 50 characters. It is best to make it short.
* Introduce what has changed, using imperatives: fix, add, modify, etc.

##### Description
* Add extra explanation if you feel it will help others to understand the summary content.
* Avoid writing in one line. Use line breaks so the reader does not have to scroll horizontally.

For more information and tips on how to write good commit messages, see the GitHub [guide](https://github.com/erlang/otp/wiki/writing-good-commit-messages).

#### Requesting a Feature
You can raise a Github issues to request new features you would like to see in SSNCE app.

* Provide a clear and detailed explanation of the feature you want and why it's important to add. 
* Keep in mind that we want features that will be useful to the majority of our users and not just a small subset.

## Contact us

#### Discord

Join us in [Discord](https://discord.gg/UZCM4Ae) community.

#### Email

Send an email to ssnmobileapp@ssn.edu.in for more queries.

## Contributors

<a href="https://github.com/ezhilnero99"><img width="40" height="40" src="https://avatars3.githubusercontent.com/u/48056173?s=460&u=ed53f94579cddecd1dd530ba015e8fdf2f84ea53&v=4"></a>&nbsp;
<a href="https://github.com/sujink1999"><img width="40" height="40" src="https://avatars3.githubusercontent.com/u/50797175?s=400&v=4"></a>&nbsp;
<a href="https://www.linkedin.com/in/amritha-sudharsan-436440164/"><img width="40" height="40" src="https://media-exp1.licdn.com/dms/image/C5603AQH6Kprp0wIxqg/profile-displayphoto-shrink_200_200/0?e=1595462400&v=beta&t=-KSpSLkK2hlBqPZ5BVQ1EkaT6SNxRPuhp8KGccIMEfg"></a>&nbsp;
<a href="https://github.com/nandy20"><img width="40" height="40" src="https://avatars3.githubusercontent.com/u/32575168?s=460&v=4"></a>&nbsp;
<a href="https://github.com/pavithrakarumanchi"><img width="40" height="40" src="https://avatars2.githubusercontent.com/u/51071573?s=460&v=4"></a>&nbsp;
<a href="http://github.com/ddlogesh"><img width="40" height="40" src="https://avatars1.githubusercontent.com/u/35095700?s=400&u=af70cbfb0ddfa4dc7068e423b94bc57c87ca87b7&v=4" style="border-radius:50%"></a>&nbsp;
<a href="https://github.com/harshavardhan98"><img width="40" height="40" src="https://avatars1.githubusercontent.com/u/20859794?s=460&u=c46728916e85915e1dab0d52f8221a51001b7d09&v=4"></a>&nbsp;
<a href="https://github.com/shrikanth7698"><img width="40" height="40" src="https://avatars2.githubusercontent.com/u/25195315?s=460&u=7e8286e59da56fbdc8dab2c190798f3aadf44e60&v=4"></a>&nbsp;
<a href="https://github.com/TarunGanesh"><img width="40" height="40" src="https://avatars0.githubusercontent.com/u/22258204?s=460&u=53a1ef305643961057e6507ea314e93575db7430&v=4"></a>&nbsp;
<a href="https://www.linkedin.com/in/shibikannan-t-m-a79493155"><img width="40" height="40" src="https://media-exp1.licdn.com/dms/image/C5103AQEWT-SaYnvZjA/profile-displayphoto-shrink_400_400/0?e=1595462400&v=beta&t=4rNk03li1PA5xYpqRndF6JWWCG0e9r9UVROwnaHYQ-A"></a>&nbsp;
<a href="https://github.com/karnikram"><img width="40" height="40" src="https://avatars2.githubusercontent.com/u/12653355?s=460&u=f58020b1d959f3f98839f8cae33289bc28b1de69&v=4"></a>&nbsp;
<a href="https://github.com/adithya321"><img width="40" height="40" src="https://avatars1.githubusercontent.com/u/3854934?s=460&v=4"></a>&nbsp;
<a href="https://github.com/varunranganathan"><img width="40" height="40" src="https://avatars0.githubusercontent.com/u/20112876?s=400&v=4"></a>&nbsp;
<a href="https://github.com/muthuct"><img width="40" height="40" src="https://avatars0.githubusercontent.com/u/12173014?s=460&u=a684ccb634f8a03df1bea0ca8bedf4fb79fa1780&v=4"></a>

## License
```
MIT License

Copyright (c) 2019 ssn-developers

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
