# A Simple Plain-text Editor

[![GitHub license](https://img.shields.io/github/license/Dragon1573/Text-Editor-JFX?label=License&style=flat-square)](https://github.com/Dragon1573/Text-Editor-JFX/blob/master/LICENSE)

## Introduction

This project is a reproduction of my Software Engineering Course Design, Semester 2. It provides an editor for users editing any kind of plain-text format files (not only `*.txt` but also `*.csv`, `*.md`, `*.rst`).

I finished my first project (`v1.0`) with Swing and Java 8 Language Level, but I think the project is too complex for me to fix and upgrade. So I reproduce this project, recover all codes with JavaFX in Java 14 Language Level. 

## User Instructions

### Requirements

- Java Runtime Environment 14 or above

### Installation

1. Install JRE 14 (any manufactures are acceptable, such as Oracle and OpenJDK) on your device.
2. Download the latest release from [GitHub Releases](https://github.com/Dragon1573/Text-Editor-JFX/releases).
3. Double-click the `*.jar` and launch the application.
4. Enjoy your work!

## Developer Guide

### Requirements

- Java Development Kit 14 (v14.0.1 is recommended) or above
- Apache Maven 3 (v3.6.3 is recommended) or above
- JetBrains IntelliJ IDEA (v2020.2 is recommended)

You can use Eclipse IDE instead of JetBrains IDE, but I can't sure if it's compatible.

### Installation

- Clone this repository to your device.
- Switch the working directory to where `pom.xml` is located in.
- Launch a terminal and execute `mvn clean compile exec:exec` to launch the application.
- Execute `mvn clean package` to create an executable, all-dependencies-included `*.jar` file.
