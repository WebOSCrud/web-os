接口访问 自动添加 wapId 前缀

## 静态资源

静态资源 自动添加 wapId 前缀

允许配置 (也会自动加wapId 前缀)

```properties
spring.web.resources.static-locations=
```

static/image.png 

- SpringBoot 访问 /image.png
- Wap 访问 /wapId/image.png 


