# Project Setup Guide

This README provides detailed instructions for setting up your development environment. Follow these steps to enable Unicode UTF-8 support on Windows and to install and configure Gradle for building and running your projects.

## Enabling Unicode UTF-8 Support

Ensuring that Unicode UTF-8 support is enabled on your system can help prevent character encoding issues across different languages and platforms.

### Windows 11

1. Search for **"Region"** in your search bar.
2. Select **"Region Settings"**.
3. Scroll down and go to **"Administrative language settings"**.
4. Select **"Change system locale"**.
5. Tick the box **"Beta: Use Unicode UTF-8 for worldwide language support"**.
6. Restart your computer.

### Windows 10

1. Go to **Control Panel**.
2. Select **"Clock and Region"**.
3. Select **"Region"**.
4. Go to the **"Administrative"** tab.
5. Select **"Change system locale"** under **"Language for non-Unicode programs"**.
6. Tick the box **"Beta: Use Unicode UTF-8 for worldwide language support"**.
7. Restart your computer.

### MacOS

1. Open up **"Terminal"**.
2. Select **"Settings"**.
3. Go to **"Encodings"** tab.
4. Tick the box **"Unicode UTF-8"**.

### Description of Project

In accordance with the project requirements, our team was tasked with designing three distinct components: Insurance, Security, and FX. These components were intended to be developed and submitted to other groups as part of a black-box experiment. However, during the design phase, we opted to integrate these components into our existing application to validate their functionalities and ensure seamless operation. Consequently, while developing the Insurance, Security, and FX components, we concurrently integrated them into our final program. This decision was made to guarantee that all features of the application function cohesively. Additionally, we received three additional components: Branch, Loan, and CreditCard, from other groups, which were seamlessly integrated into our application alongside the originally developed components.

## Setting Up Gradle

Gradle is an open-source build automation tool that is designed to be flexible enough to build almost any type of software.

### Prerequisites

- Java Development Kit (JDK) installed on your computer. Gradle requires JDK version 8 or higher.

### Installation Steps

1. **Download Gradle**:
   - Visit the [Gradle Releases Page](https://gradle.org/releases/) and download the latest distribution.

2. **Extract the Gradle Distribution**:
   - Extract the downloaded zip to a directory of your choice. This directory will be referred to as `GRADLE_HOME`.

3. **Set Environment Variables**:
   - **Windows**: Add `GRADLE_HOME\bin` to your PATH environment variable.
   - **macOS/Linux**: Add `export PATH=$GRADLE_HOME/bin:$PATH` to your shell profile.

4. **Verify Installation**:
   - Open a new terminal or command prompt and run `gradle -v` to check that the installation was successful.

### Creating a New Gradle Project

1. Navigate to your desired project directory.
2. Execute `gradle init` to create a new project.
3. Follow the prompts to configure your project setup.

### Building and Running Your Project

- To build your project, run `gradle build` within your project directory.
- To run your project, execute `gradle run`. Note that specific run commands may vary depending on your project configuration.

### Additional Resources

- [Gradle Guides](https://gradle.org/guides/) - For comprehensive documentation and tutorials.
- [Gradle Build Tool Fundamentals](https://learning.oreilly.com/library/view/gradle-build-tool/9780134757478/) - A resource for deepening your understanding of Gradle.

---
