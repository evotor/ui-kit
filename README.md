[![](https://jitpack.io/v/evotor/integration-library.svg)](https://jitpack.io/#evotor/integration-library)
[![Gitter](https://badges.gitter.im/evotor/integration-library.svg)](https://gitter.im/evotor/integration-library.svg)

В build.gradle проекта добавьте ссылку на репозиторий jitpack:

```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

в модуле `build.gradle` добавьте зависимость и укажите точную версию:

```
dependencies {
    implementation 'com.github.evotor:ui-kit:0.0.3'
}
```

и укажите minSdkVersion проекта:
```
defaultConfig {
        minSdkVersion 23
	...
}
```

В этом проекте описаны элементы интерфейса, необходимые для работы с оборудованием на смарт-терминале Эвотор.
