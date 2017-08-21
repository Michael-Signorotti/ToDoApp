# Pre-work - *Remind Me*

**Remind Me** is an android app that allows building a todo list and managing items to completion. This includes adding new items, editing and deleting an existing item.

Submitted by: **Michael Signorotti**

Time spent: **14** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **successfully add and remove items** from the todo list
* [x] User can **tap a todo item in the list and bring up an edit screen for the todo item** and then have any changes to the text reflected in the todo list.
* [x] User can **persist todo items** and retrieve them properly on app restart

The following **optional** features are implemented:

* [x] Persist the todo items [into SQLite](http://guides.codepath.com/android/Persisting-Data-to-the-Device#sqlite) instead of a text file
* [x] Improve style of the todo items in the list [using a custom adapter]
* [x] Add support for completion due dates for todo items (and display within listview item)
* [x] Use a [DialogFragment](http://guides.codepath.com/android/Using-DialogFragment) (DatePicker)
* [x] Add support for selecting the priority of each todo item (and display in listview item)
* [x] Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following **additional** features are implemented:

* [x] Utilized SearchView for improving task submission
* [x] Incorporated customized action bar features



## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://i.imgur.com/UArwNEd.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Project Analysis

As part of your pre-work submission, please reflect on the app and answer the following questions below:

**Question 1:** "What are your reactions to the Android app development platform so far? Compare and contrast Android's approach to layouts and user interfaces in past platforms you've used."

**Answer:** Developing the Android user interface was much more intuitive than I expected. The graphical drag and drop utility for adding or modifying the placement of UI elements makes creating attractive activities a breeze. Android Studio is also very helpful with its text suggestion functionality when editing the XML. I noticed similarities between Android and Spring in the area of intents, a data structure used to communicate between activities. The Spring framework has similar functionality for transferring data from the controller to a jsp. Furthermore, activity layouts, such as relative, have similar principles to their CSS counterparts. For instance, absolute positioning in CSS allows programmers to position elements relative to a particular parent. Although there are differences between how relative positioning is interpreted between Android and CSS, having this background can help one learn the new framework more quickly.

**Question 2:** "Take a moment to reflect on the `ArrayAdapter` used in your pre-work. How would you describe an adapter in this context and what is its function in Android? Why do you think the adapter is important? Explain the purpose of the `convertView` in the `getView` method of the `ArrayAdapter`."

**Answer:** An ArrayAdapter bridges the task data stored in the ArrayList with the ListView of tasks. The ListView needs formatted individual items to display, and the ArrayList contains the raw data. Since the adapter takes both the individual Listview and the ArrayList as an input, it constructs views for each of the items which the ListView then displays to the user. The design of the adapter is important because it abstracts away boilerplate code and allows developers to focus on the logic and design of the application.

The purpose of the convertView in the getView method of the ArrayAdapter is to allow the adapter to create new views from ones no longer being used with the aim of maximizing application performance. Suppose a ListView contains too many list items to fit on the screen. As a user scrolls, new ListViews will need to be produced. If the Android framework did not have convertView as an option, a new ListView would need to be created for each new combination of items a user chooses to display on the screen. Since this is memory intensive and could dampen system performance, reusing views through convertView allows the Android framework to perform view updates efficiently, and consequently, provide a better user experience.


## Notes

Describe any challenges encountered while building the app.

One challenge I faced was updating the SQLite database with a standard update query formatted as a string. Values were not being stored properly, and SQLite was throwing errors writing information. I resolved this issue by using ContentValues when updating records.


## License

    Copyright [2017] [Michael Signorotti]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.