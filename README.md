# ScrollingFeed
[![Release](https://jitpack.io/v/jmilham21/scrollingfeed.svg?style=flat-square)](https://jitpack.io/v/jmilham21/scrollingfeed)

This application shows enhanced implementation of JW Player's Android SDK and Delivery API to create a seamless scrolling video content application.
!JW Winter HackWeek 2023!

## Requirements

* A JW account with valid SDK license key.
* A JW Playlist formatted with portrait content 

## Getting Started - Demonstration
The accompanying application is a super basic demonstration intended to showcase a simple implementation.
1. Clone the repo
2. Add JW SDK License key to `MainActivity.kt`
   1. `LicenseUtil().setLicenseKey(this, "TODO")`
   2. Required for any usage of the JW player
3. Supply the `JwTikTakFragment.newInstance` constructor with a valid JW Playlist ID

## Getting Started - Module modification
This module was built with a lot of assumptions and for specific content. Tweak as needed

Things to note:
* The module isn't designed for non-portrait content
  * Stretching occurs which will not be ideal for all content
* The module isn't designed for content coming from outside the JW platform
  * Content is parsed in form of a JW PlaylistId
* All of the above can easily be changed via a fork or PR

## Still to do
* Allow for more customization without module code changes
* Potentially open up for more content sources
* Add a drop in middleware endpoint for things like likes, share, watch later, etc. 

### Way down the road
* Implement Identity Management via InPlayer technologies
