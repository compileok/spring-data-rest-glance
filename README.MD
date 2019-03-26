
近日发现了spring-data-rest项目，于是创建这个spring-data-rest-glance来体验一下。

本例使用springboot，并使用了 spring-data-rest 和 spring-data-jpa

此二者结合：**真的可以实现10分钟创建一个rest应用**

我们创建一个`person`表，并创建`person`的`entity`和`repository`，让`repository`继承`JpaRepository`。

关键的来了：`@RepositoryRestResource(collectionResourceRel = "person", path = "person")`
我们给`repository`加上一个`@RepositoryRestResource`注解，我们来启动项目，看看此注解的魔力。

## 启动项目
访问：`htttp://localhost:8000/person`` 得到的结果如下：
```json
{
    "_embedded": {
        "person": []
    },
    "_links": {
        "self": {
            "href": "http://localhost:8000/person{?page,size,sort}",
            "templated": true
        },
        "profile": {
            "href": "http://localhost:8000/profile/person"
        },
        "search": {
            "href": "http://localhost:8000/person/search"
        }
    },
    "page": {
        "size": 20,
        "totalElements": 0,
        "totalPages": 0,
        "number": 0
    }
}
```
我们看到 `person`节点并无内容。
## 添加person
我们使用POST方式访问 `http://localhost:8000/person` 并提交如下 JSON 数据：
```json
{"firstName": "tomcat", "lastName": "cat"}
```
## 查看person 及 person 列表
我们再次在浏览器中访问（GET）  `http://localhost:8000/person`。得到的结果中，JSON数据和第一步中一样，person节点中不再是空的了。
```json
[
    {
        "firstName": "tomcat",
        "lastName": "cat",
        "_links": {
            "self": {
                "href": "http://localhost:8000/person/1"
            },
            "person": {
                "href": "http://localhost:8000/person/1"
            }
        }
    }
]
```
我们可以继续多添加几条数据，方便下面展示查询。在添加多条信息之后，如果想查看某个person的详情，例如：`http://localhost:8000/person/7`
```json
{
    "firstName": "李",
    "lastName": "四",
    "_links": {
        "self": {
            "href": "http://localhost:8000/person/7"
        },
        "person": {
            "href": "http://localhost:8000/person/7"
        }
    }
}
```
## 条件查询
假设我们需要根据用户名查询用户，我们在`PersonRepository`中添加一个方法`findByLastName`.
托spring-data-jpa的福，我们只需要写这样的一行代码，然后什么都不用做，spring-data-jpa会解析`findByLastName`并应用到查询上。
```java
List<Person> findByLastName(@Param("name") String name);
```
写好上面的代码之后，我们重启项目，访问`http://localhost:8000/person/search`结果如下：
```json
{
    "_links": {
        "findByLastName": {
            "href": "http://localhost:8000/person/search/findByLastName{?name}",
            "templated": true
        },
        "findByFirstName": {
            "href": "http://localhost:8000/person/search/findByFirstName{?name}",
            "templated": true
        },
        "self": {
            "href": "http://localhost:8000/person/search"
        }
    }
}
```
我们可以看到，这里已经列出了当前可用的search方法。我们访问：`http://localhost:8000/person/search/findByLastName?name=cat`
```json
{
    "person": [
        {
            "firstName": "tomcat",
            "lastName": "cat",
            "_links": {
                "self": {
                    "href": "http://localhost:8000/person/1"
                },
                "person": {
                    "href": "http://localhost:8000/person/1"
                }
            }
        },
        {
            "firstName": "tom",
            "lastName": "cat",
            "_links": {
                "self": {
                    "href": "http://localhost:8000/person/2"
                },
                "person": {
                    "href": "http://localhost:8000/person/2"
                }
            }
        }
    ]
}
```
我们可以看到，这里通过`findByLastName?name=cat`找到了两个人：tomcat cat 和 tom cat

## 分页查询
为了演示分页，我们先多添加几条用户数据。在第一步中展示的结果中，我们可以看到这样的一行数据：
``
http://localhost:8000/person{?page,size,sort}
``
这提示了我们分页的使用方法，我们来访问`http://localhost:8000/person?page=2&size=3` 试试，即：访问第2页数据，页大小是3。
下面贴出 关键结果的节点：
```json
{
    "_embedded": {
        "person": [
            {
                "firstName": "李",
                "lastName": "四",
                "_links": {
                    "self": {
                        "href": "http://localhost:8000/person/7"
                    },
                    "person": {
                        "href": "http://localhost:8000/person/7"
                    }
                }
            },
            {
                "firstName": "王",
                "lastName": "五",
                "_links": {
                    "self": {
                        "href": "http://localhost:8000/person/8"
                    },
                    "person": {
                        "href": "http://localhost:8000/person/8"
                    }
                }
            }
        ]
    },
    "page": {
        "size": 3,
        "totalElements": 8,
        "totalPages": 3,
        "number": 2
    }
}
```
确实查到了数据，结果对不对呢？根据上面的从上面的结果看出，我们添加8条数据，页大小是3，所以：
``
总页数 = 3
第一页  3条数据
第二页  3条数据
第三页  2条数据
``
我们访问的是，`http://localhost:8000/person?page=2&size=3`page=2,但是实际上取的是2条数据——是第3页。那么**页码其实是从0开始**的对吗？
我们继续访问 `http://localhost:8000/person?page=0&size=3` `http://localhost:8000/person?page=1&size=3`
可以发现，确实是如此，**页码从0开始**。any way 

## controller 去哪里了

到目前为止，我们只写了很少的代码，但是却已经实现了增删改查。我们甚至连 controller都没有写，就访问了这么多的rest url。
我们只通过`@RepositoryRestResource(collectionResourceRel = "person", path = "person")`在 dao 中就能够把 /path路径暴露出来。
边一切都有了，这就是spring-data-rest的魔力。

## 自定义 spring-data-rest 魔力之外的controller可以吗

当然可以了，上面我们所访问的 /person/* 的地址，是从dao中通过 `@RepositoryRestResource` 注解暴露出去的。
那么现在我们就手写一个controller，访问路径也叫/person,即：`@RequestMapping("/person")`

```java
@Controller
@RequestMapping("/person")
public class PersonController {

	@RequestMapping("/hello")
	@ResponseBody
	public String hello(){
		return " Hello,welcome to the normal controller! ";
	}

}
``` 
我们自己创建的controller访问路径也是，`/person` 还创建了一个自定义的 `hello`方法，这个`/person` 和dao里边暴露的`/person`
能共存，并和谐相处吗？我们访问看看：`http://localhost:8000/person/hello` 我们在浏览器中可以看到：
```
Hello,welcome to the normal controller!
```

## 总结
至此，我们体验了一下 spring-data-rest。总有“刁民”说java开发很慢，代码太多了。多吗？不多啊，我们这里使用spring-data-jpa
加上spring-data-rest，只写了很少的代码就实现了大部分基础的功能了。下次开发新项目，可以尝试使用 spring-data-rest加spring-data-jpa了。
