# 🗃️ Kedis - A Kotlin Redis client.

A very experimental Redis client that is written in [Kotlin](https://kotlinlang.org/)

- [**Discord Server**](https://mixtape.systems/development)

## 🚀 Installation

#### 🐘 Install w/ Gradle

```kt
repositories {
    maven("https://maven.dimensional.fun/releases")
}

dependencies {
    implementation("mixtape.oss.kedis:kedis-core:{VERSION}")
}
```

#### 🪶 Install w/ Maven

```xml
<repositories>
    <repository>
        <id>dimensional-maven</id>
        <name>Dimensional Maven</name>
        <url>https://maven.dimensional.fun/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>mixtape.oss.kedis</groupId>
        <artifactId>kedis-core</artifactId>
        <version>{VERSION}</version>
    </dependency>
</dependencies>
```

## 💻 Usage

#### Getting a Redis client

You can get a Redis client in two ways:
1. Creating an instance of `RedisClient` yourself:
```kt
val client = RedisClient("redis://127.0.0.1")
```
2. Using a `RedisPool`:
```kt
val pool = RedisPool("redis://127.0.0.1", initialSize = 5)
val client: RedisClient = pool.get()
```

---

Mixtape Bot 2019-2022
