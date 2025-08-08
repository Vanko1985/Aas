FROM mcr.microsoft.com/devcontainers/base:ubuntu-22.04

# Инсталиране на Java 17
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# Инсталиране на Android SDK
RUN mkdir -p /usr/lib/android-sdk/cmdline-tools/latest && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O /tmp/cmdline-tools.zip && \
    unzip /tmp/cmdline-tools.zip -d /usr/lib/android-sdk/cmdline-tools/latest && \
    mv /usr/lib/android-sdk/cmdline-tools/latest/cmdline-tools/* /usr/lib/android-sdk/cmdline-tools/latest/ && \
    rm -rf /tmp/cmdline-tools.zip

# Приемане на лицензи и инсталиране на платформи
RUN yes | /usr/lib/android-sdk/cmdline-tools/latest/bin/sdkmanager --licenses && \
    /usr/lib/android-sdk/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"

# Настройка на Gradle
ENV GRADLE_USER_HOME=/workspaces/.gradle
