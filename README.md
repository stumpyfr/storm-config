# storm-config #
Currently under development

## description ##
Just a helper to load your storm's topology and configuration from a json file

It currently provides TopologyLoader to help you to load your topology from a json file who describes everything.
It allows you to manage your topology and your parallelism configuration for each spout/bolt.

```javascript
{
	"name": "test",
	"spouts": [
		{
			"id" : "SpoutMQ",
			"class": "com.tekforge.StormSample.Spouts.SpoutMQ",
			"task": 2
		}
	],
	"bolts": [
		{
			"id" : "Check",
			"class": "com.tekforge.StormSample.Bolts.CheckBolt",
			"task": 3,
			"shuffleGrouping" : [
				{
					"id": "SpoutMQ"
				}
			]
		},
		{
			"id" : "Test",
			"class": "com.tekforge.StormSample.Bolts.TestBolt",
			"task": 3,
			"fieldsGrouping" : [
				{
					"id": "Check",
					"fields": [
						{
							"value" : "value1"
						},
						{
							"value" : "value2"
						}
					]
				}
			]
		}
	]
}
```

```java
TopologyBuilder builder = TopologyLoader.Build(args[0]); // example "conf.json"
String topologyName = TopologyLoader.GetName();
Config conf = new Config();
conf.setNumWorkers(8);
StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
```
Start your topology:
```
./storm-0.8.2/bin/storm jar MyProject.jar com.tekforge.StormSample.App config.json
```

# Usage #

To produce a jar:

    $ mvn package

To install in your local Maven repository:

    $ mvn install

To use in your `pom.xml`:

```xml
<project>
  <!-- ... -->
  <dependencies>
    <!-- ... -->
    <dependency>
      <groupId>com.tekforge</groupId>
      <artifactId>storm-config</artifactId>
      <version>0.0.1</version>
    </dependency>
    <!-- ... -->
  </dependencies>
  <!-- ... -->
</project>
```



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/stumpyfr/storm-config/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

