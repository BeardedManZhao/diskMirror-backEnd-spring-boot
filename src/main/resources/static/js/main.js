const jokerBoxPopUp = new JokerBox_popUp(document.getElementById("show_area"));

function transferDeposit() {
    const s = prompt("请输入您要转存的文件的 url");
    if (s) {
        const fileName = prompt("请输入您要转存的文件在盘镜中的名字");
        if (fileName) {
            jokerBoxPopUp.show("转存任务已提交，转存表中可查队列信息哦!")
            if (!isShowTransferDeposit_fileList_table) {
                showTransferDeposit_fileList_table(document.getElementById("showTransferDeposit_fileList_table_button"));
            }
            diskMirror.transferDeposit(
                {
                    userId: userId,
                    type: indexConfig.spaceType,
                    fileName: fileName,
                    url: s,
                },
                (data) => {
                    jokerBoxPopUp.show(data.fileName + " 转存成功!!! 刷新可见!!");
                },
                (e) => {
                    jokerBoxPopUp.show(fileName + " 转存失败：" + JSON.stringify(e));
                }
            )
        }
    }
}

function reloadFsPath(element) {
    let searchParams = DiskMirrorFront.search_Params("path");
    if (element) {
        if (searchParams.length !== 0 && searchParams[searchParams.length - 1] === element) {
            // 代表不需要追加参数
            window.location.href = window.location.href.replaceAll('#', '');
            return;
        }
        window.location.href = window.location.href.replaceAll('#', '') + "&path=" + element;
    } else {
        // 获取到当前层级
        const value = document.querySelector("#diskMirrorPathInput").value;
        reloadFsPath(value ? value : '/');
    }
}

let isShowTransferDeposit_fileList_table = false;

function showTransferDeposit_fileList_table(b) {
    if (isShowTransferDeposit_fileList_table) {
        document.querySelectorAll("#transferDeposit_fileList_table").forEach((element) => element.style.display = 'none');
        b.innerText = ' 转存表';
        isShowTransferDeposit_fileList_table = false;
    } else {
        document.querySelectorAll("#transferDeposit_fileList_table").forEach((element) => element.style.display = 'table');
        b.innerText = ' 转存表'
        isShowTransferDeposit_fileList_table = true;
    }
}

const progressBar = new ProgressBar(document.querySelector(".progress-bar"), (now, max) => `您的盘镜空间使用量为：${DiskMirrorFront.formatBytes(now)}/${DiskMirrorFront.formatBytes(max)}；占比为：${(now / max * 100).toFixed(2)}%`);
let diskMirror = new DiskMirror(indexConfig.server);
// 获取到id 和 口令
const searchParams1 = [DiskMirrorFront.getLatestCookieValue('diskMirror_server_pass')]
const searchParams0 = DiskMirrorFront.search_Params("server_id");
if (searchParams0.length === 0) {
    alert("请输入您的空间id");
    window.location.href = "index.html";
}
if (searchParams1[0] === null) {
    alert("请输入您的空间信息");
    window.location.href = "index.html";
}
const userId = searchParams0[searchParams0.length - 1];
const type = indexConfig.spaceType ? indexConfig.spaceType : 'Binary';

document.querySelector("title").innerText = `盘镜${userId}号 空间文件管理器`;

function mkdir() {
    const s = prompt("您要创建的目录名字？", '/xxx/xxx');
    if (!s) {
        return;
    }
    if (s.length !== 0) {
        diskMirror.mkdirs(userId, type, s, (res) => {
            jokerBoxPopUp.show(`${res.fileName} 目录创建成功!! 请刷新查看哦!!!`);
        }, (res) => {
            jokerBoxPopUp.show(`目录创建失败!!${JSON.stringify(res)}`);
        })
    }
}

function extractedFsList(res) {
    if (res.length === 0) {
        jokerBoxPopUp.show("空空如也，快去创建一个吧！");
        res = [
            {
                "fileName": "(^~^) 现在没有文件，快去创建一个吧！",
                "url": "https://diskmirror.lingyuzhao.top//29/Binary//Article/Image/bigImage.jpg",
                "lastModified": new Date().getTime(),
                "size": 0,
                "type": "Binary",
                "isDir": false
            }
        ]
    }
    return new FS_List('fileName',
        res,
        document.getElementById("fileList_table"),
        (f) => {
            if (f.isDir) {
                jokerBoxPopUp.show(f.fileName + " 是一个文件夹。");
                return;
            }
            diskMirror.downLoad(userId, type, f.fileName, (res) => {
                window.open(res)
            })
        }
        ,
        (f) => {
            diskMirror.remove(
                userId,
                type,
                f.fileName,
                (res) => {
                    jokerBoxPopUp.show(res.fileName + " 删除成功!!!");
                })
        }
        ,
        (f) => {
            const s = prompt("您期望将其重命名为？", f.fileName);
            if (!s) {
                return;
            }
            diskMirror.reName(
                userId, type, f.fileName, s,
                (res) => {
                    jokerBoxPopUp.show(res.fileName + " 重命名成功!!! 刷新可见新数据!");
                }
            )
        },
        document.querySelector("#diskMirrorPathInput"),
        f => diskMirror.downLoad(userId, type, f.fileName, (res) => window.open('preView.html?url=' + res)),
    );
}

window.onload = function () {
    let fsList;
    try {
        diskMirror.setSk(parseInt(searchParams1[searchParams1.length - 1]), indexConfig.domain)
        diskMirror.getUrls(userId, type,
            (res) => {
                progressBar.setProgressByValue(res['useSize'], res['maxSize'], 'linear-gradient(to right, rgba(255, 255, 255, 0.5), #8c00ff)')
                fsList = extractedFsList(res['urls']);
                // 判断是否需要路径
                const searchParams = DiskMirrorFront.search_Params('path');
                if (searchParams.length > 0) {
                    fsList.setPath(searchParams[searchParams.length - 1]);
                }
            },
            (e) => {
                progressBar.setProgressByValue(0, 'linear-gradient(to right, rgba(255, 255, 255, 0.5), #8c00ff)')
                if ('res' in e) {
                    if (e['res'].endsWith("不可读!!!")) {
                        // 这里代表用户是第一次使用 盘镜 所以可能没有目录 但并不代表是发生了错误!!!
                        fsList = extractedFsList([])
                        // 判断是否需要路径
                        const searchParams = DiskMirrorFront.search_Params('path');
                        if (searchParams.length > 0) {
                            fsList.setPath(searchParams[searchParams.length - 1]);
                        }
                        return;
                    } else {
                        progressBar.setProgressByValue(124 << 10 << 10, 128 << 10 << 10, 'linear-gradient(to right, rgba(255, 255, 255, 0.5), #8c00ff)');
                    }
                    console.error(e);
                    new FS_List('fileName', [
                        {
                            fileName: '发生了错误，盘镜资源加载失败: ' + e['res'],
                            fileSize: 0,
                            fileType: 'error',
                            isDir: false
                        },
                    ], document.getElementById("fileList_table"))
                    jokerBoxPopUp.show(e['res'])
                    return;
                }
                new FS_List('fileName', [
                    {
                        fileName: '发生了错误，服务器拒绝操作: ' + e,
                        fileSize: 0,
                        fileType: 'error',
                        isDir: false
                    }
                ], document.getElementById("fileList_table"))
                console.error(e);
                jokerBoxPopUp.show(e)
            })

        const transferDeposit_fileList_table = document.querySelector("#transferDeposit_fileList_table tbody");

        // 获取状态灯
        const status_bar = document.getElementsByClassName("status_bar");

        for (let statusBarElement of status_bar) {
            statusBarElement.addEventListener("click", function (){
                if (statusBarElement.style.color === 'red'){
                    // 代表停止 在这里重新连接
                    diskMirror.setController('/FsCrud');
                    jokerBoxPopUp.show("正在重新连接服务器...");
                } else {
                    // 代表启动 在这里断开连接
                    diskMirror.setController("---------------");
                    jokerBoxPopUp.show("正在断开服务器连接...");
                }
            })
        }

        // 转存查询
        setInterval(() => {
            diskMirror.transferDepositStatus({userId: userId, type: type}, (res) => {
                const date = DiskMirrorFront.getDate(new Date());
                document.querySelector("body").className = '';
                for (let statusBarElement of status_bar) {
                    statusBarElement.style.color = '#00ffae';
                    statusBarElement.title = '服务器正常，最近的检查时间：' + date;
                }
                transferDeposit_fileList_table.innerHTML = '';
                for (const fileName in res) {
                    const tr = document.createElement("tr");
                    tr.className = "row0";
                    const td0 = document.createElement("td");
                    const span0 = document.createElement("span");
                    span0.innerText = '';
                    span0.className = 'load-icon'
                    const span1 = document.createElement("span");
                    span1.innerText = ' ' + fileName;
                    td0.appendChild(span0);
                    td0.appendChild(span1);
                    tr.appendChild(td0);
                    const td1 = document.createElement("td");
                    td1.innerText = res[fileName];
                    tr.appendChild(td1);
                    transferDeposit_fileList_table.appendChild(tr);
                }
            }, (_) => {
                document.querySelector("body").className = 'errorBody';
                for (let statusBarElement of status_bar) {
                    statusBarElement.style.color = 'red';
                    statusBarElement.title = '目前无法获取到与服务器的通信！';
                }
                if(isShowTransferDeposit_fileList_table) {
                    jokerBoxPopUp.show('无法与转存状态服务连接，请检查网络或diskMirror服务器版本是否 >= 1.2.0')
                }
            });
        }, 5000)

        document.querySelector("#diskMirrorBackPath").addEventListener("click", () => fsList.toBackPath());

        document.getElementById("diskMirrorSearch").addEventListener("keydown", (e) => {
            if (e.key === 'Enter') {
                const searchStr = document.getElementById("diskMirrorSearch").value;
                jokerBoxPopUp.show('正在当前目录搜索文件名包含 ' + searchStr + ' 的文件！')
                fsList.filter(
                    d => searchStr.length === 0 || d.fileName.includes(searchStr)
                );
            }
        })

        document.getElementById("fileUpload").addEventListener("change", function (e) {
            const files = e.target.files;
            // 获取到当前层级
            const element = document.querySelector("#diskMirrorPathInput").value;
            // 成功记录
            let count = 0;
            let noWarCount = 0;
            for (const file of files) {
                if (file.name.endsWith(".war")) {
                    jokerBoxPopUp.show(`检测到 war 文件，正在将 《${file.name}》 自动解析为一个同名的目录！`)
                    // 代表是 war 文件 需要被解析一下
                    Utils_io.unzipFile(file, (c, f, path, backOk) => {
                        diskMirror.upload({
                            fileName: element + '/' + file.name.substring(0, file.name.length - 4) + '/' + path,
                            userId: userId,
                            type: indexConfig.spaceType
                        }, f, (res) => {
                            res.lastModified = new Date().getTime()
                            jokerBoxPopUp.show(`${file.name} 文件中：批量上传了 ${c} 个文件。`);
                            backOk[0] = true;
                        }, e => {
                            backOk[0] = true;
                            jokerBoxPopUp.show(`${file.name} 中 有文件解压失败!!! ${JSON.stringify(e)}`);
                            console.error(e)
                        })
                    }, () => {
                        jokerBoxPopUp.show(`${file.name} 解压完毕!!!`);
                        if (++noWarCount === files.length) {
                            reloadFsPath(element);
                        }
                    })
                    ++count;
                    return;
                }
                diskMirror.upload({
                    fileName: element + '/' + file.name,
                    userId: userId,
                    type: indexConfig.spaceType
                }, file, (res) => {
                    res.lastModified = new Date().getTime()
                    jokerBoxPopUp.show(`共${files.length} 个文件！目前批量上传了 ${++noWarCount} 个文件。`);
                    if (noWarCount === files.length) {
                        reloadFsPath(element);
                    }
                }, undefined)
            }
        })
    } catch (e) {
        console.error(e);
        new FS_List('fileName', [
            {
                fileName: '发生了错误，盘镜资源处理失败: ' + e,
                fileSize: 0,
                fileType: 'error',
                isDir: false
            }
        ], document.getElementById("fileList_table"))
    }
}