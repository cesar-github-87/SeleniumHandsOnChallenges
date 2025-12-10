FROM maven:3.9.9-amazoncorretto-21-al2023

# Install Chrome without gpg (using apt-key deprecation workaround)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        wget \
        unzip && \
    # Download Chrome .deb directly (no GPG needed) \
    wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt-get install -y ./google-chrome-stable_current_amd64.deb && \
    rm google-chrome-stable_current_amd64.deb && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy your test code
COPY . /app
WORKDIR /app

# Verify installations
RUN mvn -version && google-chrome --version