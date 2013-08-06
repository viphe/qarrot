Qarrot: a modest Java queue-app framework (pre-alpha)
=====================================================

Qarror is an attempt at providing a base for building RabbitMQ-enabled application in a way similar
to webapps with Jersey. It's kinda RabbitMQ hijacking JAX-RS.

It is very much in an experimental state for now, but here is an example of a resource class:

```java
public class SantaClausMailbox {

    public List<String> letters = new ArrayList<String>();

    @Path("santa_claus")
    @Consumes(MediaType.TEXT_PLAIN)
    public void receiveLetter(String letter) {
        letters.add(letter);
    }
}
```

Current Goals:
--------------

- support of the main patterns of RabbitMQ (worker, topic, RPC, etc)
- easy route (i.e. exchanges, queues, etc) declaration (at least, easy in simple cases)
- resource classes (consumer)
- auto-reconnection (client and server sides)
- support for message persistence and resume when connectivity is reestablished (client side)
- content chunking
- [maybe] publishing interfaces ==> equivalent to resources but on the client side
