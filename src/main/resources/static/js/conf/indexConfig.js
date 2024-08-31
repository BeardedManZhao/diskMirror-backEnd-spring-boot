const indexConfig = {
    // 服务器地址
    server: undefined,
    // 空间类型
    spaceType: "Binary",
    // 盘镜 服务器所处的域
    domain: undefined,
}

// 获取完整的URL
const url = window.location.href;

// 解析URL
const parser = document.createElement('a');
parser.href = url;

// 获取协议
const protocol = parser.protocol;

// 获取主机地址
const host = parser.hostname;

// 获取端口号
const port = parser.port;

// 输出结果
console.log("协议: " + protocol);
console.log("主机地址: " + host);
console.log("端口号: " + port);
indexConfig.server = protocol + '//' + host + ':' + port;