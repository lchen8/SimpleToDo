# Pre-work - *Simple To-Do*

**Simple To-Do** is an android app that allows building a todo list and basic todo items management functionality including adding new items, editing and deleting an existing item.

Submitted by: **Lily**

Time spent: **16** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **successfully add and remove items** from the todo list
* [x] User can **tap a todo item in the list and bring up an edit screen for the todo item** and then have any changes to the text reflected in the todo list.
* [x] User can **persist todo items** and retrieve them properly on app restart

The following **optional** features are implemented:

* [ ] Persist the todo items [into SQLite](http://guides.codepath.com/android/Persisting-Data-to-the-Device#sqlite) instead of a text file
* [ ] Improve style of the todo items in the list [using a custom adapter](http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView)
* [ ] Add support for completion due dates for todo items (and display within listview item)
* [ ] Use a [DialogFragment](http://guides.codepath.com/android/Using-DialogFragment) instead of new Activity for editing items
* [ ] Add support for selecting the priority of each todo item (and display in listview item)
* [x] Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following **additional** features are implemented:

* [x] Clear entire list
* [x] Used AlertDialogs for editting/deleting items
* [x] Added menu items on the action bar
* [x] Change background color theme
* [x] Allow users to create, delete, and switch between different lists
* [x] Change item text style to bold, strike-through, and normal

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

* Required functionality: add/remove/edit items, preserve items between sessions

<img src='http://g.recordit.co/l7DLhtgI0G.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

* Extra functionality: clear list, pop-up editor, change background color, manage multiple lists, change text style

Part 1

<img src='http://g.recordit.co/5kcCJFE7xk.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

Part 2

<img src='http://g.recordit.co/Rsb1EqVtUL.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

## Notes

A bit overwhelming with how many pieces have to be put together in Java, but it made more sense as I went along.
File I/O was hard to test, but I did lots of logging to get it to a functional state.
There are lots of different ways to do the same thing (Toolbar vs Actionbar for example), and different sources recommended different approaches.

## License

    Copyright [2016] [Lily Chen]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
