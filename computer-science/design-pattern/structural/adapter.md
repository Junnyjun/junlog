# Adapter

Adapter 패턴은 기존 클래스(또는 객체)가 제공하는 인터페이스를 클라이언트가 요구하는 다른 인터페이스로 변환해주는 구조적 디자인 패턴입니다.

```
   Client
     │
     ▼
  [Target]
     ▲
     │
 [Adapter] ────→ [Adaptee]
```

즉, 기존 클래스의 기능은 그대로 사용하면서도 클라이언트가 기대하는 방식으로 인터페이스를 맞춰주어, 두 시스템 간의 호환성을 높일 수 있습니다.

* **타겟(Target)**: 클라이언트가 사용하는 인터페이스입니다.
* **어댑티(Adaptee)**: 기존에 존재하는, 원하는 인터페이스와 다른 인터페이스를 가진 클래스입니다.
* **어댑터(Adapter)**: 어댑티의 인터페이스를 타겟 인터페이스로 변환하는 클래스입니다.

***

### How do code

{% tabs %}
{% tab title="JAVA" %}
```java
// Target 인터페이스: 클라이언트가 사용하고자 하는 인터페이스
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee 클래스: 기존의 인터페이스를 가진 클래스
public class AdvancedMediaPlayer {
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file. Name: " + fileName);
    }
    
    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file. Name: " + fileName);
    }
}
// Adapter 클래스: Adaptee의 기능을 Target 인터페이스로 변환합니다.
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMediaPlayer;
    
    public MediaAdapter(String audioType) {
        advancedMediaPlayer = new AdvancedMediaPlayer();
    }
    
    @Override
    public void play(String audioType, String fileName) {
        if(audioType.equalsIgnoreCase("vlc")) {
            advancedMediaPlayer.playVlc(fileName);
        } else if(audioType.equalsIgnoreCase("mp4")) {
            advancedMediaPlayer.playMp4(fileName);
        }
    }
}
// AudioPlayer는 Target 인터페이스(MediaPlayer)를 구현하여 사용합니다.
public class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;
    
    @Override
    public void play(String audioType, String fileName) {
        if(audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing mp3 file. Name: " + fileName);
        } else if(audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Invalid media. " + audioType + " format not supported.");
        }
    }
}

```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
/// Target 인터페이스
interface MediaPlayer {
    fun play(audioType: String, fileName: String)
}

// Adaptee 클래스
class AdvancedMediaPlayer {
    fun playVlc(fileName: String) {
        println("Playing vlc file. Name: $fileName")
    }

    fun playMp4(fileName: String) {
        println("Playing mp4 file. Name: $fileName")
    }
}
// Adapter 클래스
class MediaAdapter(private val audioType: String) : MediaPlayer {
    private val advancedMediaPlayer = AdvancedMediaPlayer()

    override fun play(audioType: String, fileName: String) {
        when (audioType.lowercase()) {
            "vlc" -> advancedMediaPlayer.playVlc(fileName)
            "mp4" -> advancedMediaPlayer.playMp4(fileName)
        }
    }
}
class AudioPlayer : MediaPlayer {
    private var mediaAdapter: MediaAdapter? = null

    override fun play(audioType: String, fileName: String) {
        when (audioType.lowercase()) {
            "mp3" -> println("Playing mp3 file. Name: $fileName")
            "vlc", "mp4" -> {
                mediaAdapter = MediaAdapter(audioType)
                mediaAdapter?.play(audioType, fileName)
            }
            else -> println("Invalid media. $audioType format not supported.")
        }
    }
}
```
{% endtab %}
{% endtabs %}
