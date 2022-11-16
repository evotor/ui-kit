[![](https://jitpack.io/v/evotor/ui-kit.svg)](https://jitpack.io/#evotor/ui-kit)
[![Gitter](https://badges.gitter.im/evotor/ui-kit.svg)](https://gitter.im/evotor/ui-kit.svg)

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
    implementation 'com.github.evotor:ui-kit:0.0.8'
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
