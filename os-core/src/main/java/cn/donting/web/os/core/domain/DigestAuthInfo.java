package cn.donting.web.os.core.domain;

import lombok.Data;

@Data
public class DigestAuthInfo {
    private String username;//认证的用户名
    private String realm;//Web服务器中受保护文档的安全域（比如公司财务信息域和公司员工信息域），用来指示需要哪个域的用户名和密码
    private String nonce;//服务端向客户端发送质询时附带的一个随机数
    private String uri;//请求的资源位置,访问地址，例如/index
    private String response;//由用户代理软件计算出的一个字符串，以证明用户知道口令
    private String qop;//保护质量，包含auth（默认的）和auth-int（增加了报文完整性检测）两种策略，（可以为空，但是）不推荐为空值
    private String nc;//nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量。
    public String cnonce;//客户端随机数，这是一个不透明的字符串值，由客户端提供，并且客户端和服务器都会使用，以避免用明文文本。

}