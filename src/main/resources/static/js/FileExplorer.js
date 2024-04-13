/* 使用 JavaScript 随机生成并应用角度值（由于 CSS 本身无法实现真正的随机角度渐变） */
document.addEventListener('DOMContentLoaded', () => {
    const randomAngle = Math.floor(Math.random() * 360);
    document.documentElement.style.setProperty('--random-angle', randomAngle + 'deg');
});

class Checkbox {
// 

    #index;
    #dataJson = {};
    #length = 0;
    #checkJson = {};
    #noCheck_a_list = {};
    #check_a_list = {};
    #check_a_list_length = 0;
    #allCheck;

    /**
     * 追加
     * @param index_key 当前数据容器中的 做为索引的列名
     * @param dataList 当前数据容器的数据，是个列表 列表每个元素都是json json 的key 是列名字
     * @param dom 承载数据的 dom 一般是一个table对象
     * @param downloadClick 点击下载按钮之后的操作，输入的是dataObj
     * @param deleteClick 点击删除按钮之后的操作，输入的是dataObj
     * @param reNameClick 点击重命名按钮之后的操作，输入的是dataObj
     * @param trClick 点击tr之后的操作，输入的是dataObj
     * @param preView 点击预览按钮之后的操作，输入的是dataObj
     */
    constructor(index_key, dataList, dom, downloadClick, deleteClick, reNameClick, trClick, preView) {
        this.#index = index_key;
        this.dom = dom;
        this.dom_body = document.createElement('tbody');
        this.downloadClick = downloadClick;
        this.deleteClick = deleteClick;
        this.reNameClick = reNameClick;
        this.trClick = trClick;
        this.previewClick = preView;
        this.sortModel = {};

        // 初始化表头
        // 创建新的 <thead> 元素
        const theadElement = document.createElement('thead');

        // 创建新的 <tr> 元素
        const trElement = document.createElement('tr');

        // 创建并追加第一个 <th> 元素（带有 <a> 标签）
        const firstThElement = document.createElement('th');
        const clickIconLink = document.createElement('a');
        clickIconLink.className = 'click_icon okClick';
        clickIconLink.href = '#';
        clickIconLink.innerHTML = '';
        clickIconLink.addEventListener("click", () => {
            if (this.#allCheck.innerText === '') {
                // 未全选 直接设为全选
                this.#allCheck.innerText = '';
                for (const key in this.#noCheck_a_list) {

                    if (this.#dataJson[key].isDir) {
                        continue;
                    }
                    this.#noCheck_a_list[key].innerText = '';
                    this.#noCheck_a_list[key].click();
                }
            } else {
                this.#allCheck.innerText = '';
                for (const key in this.#check_a_list) {

                    if (this.#dataJson[key].isDir) {
                        continue;
                    }
                    this.#check_a_list[key].innerText = '';
                    this.#check_a_list[key].click();
                }
            }
        })
        this.#allCheck = clickIconLink;
        firstThElement.appendChild(clickIconLink);
        trElement.appendChild(firstThElement);

        // 创建并追加其余 <th> 元素
        const secondThElement = document.createElement('th');
        secondThElement.textContent = '文件名';
        secondThElement.addEventListener("click", () => {
            const res = Object.values(this.#dataJson);
            if (this.sortModel['fileName']) {
                res.sort((e1, e2) => e2.fileName.localeCompare(e1.fileName))
                this.sortModel['fileName'] = false;
            } else {
                res.sort((e1, e2) => e1.fileName.localeCompare(e2.fileName))
                this.sortModel['fileName'] = true;
            }
            this.clear();
            this.setData(res);
        })
        trElement.appendChild(secondThElement);

        const thirdThElement = document.createElement('th');
        thirdThElement.textContent = '文件类型';
        thirdThElement.addEventListener("click", () => {
            const res = Object.values(this.#dataJson);
            if (this.sortModel['isDir']) {
                res.sort((e1, e2) => e2.isDir - e1.isDir)
                this.sortModel['isDir'] = false;
            } else {
                res.sort((e1, e2) => e1.isDir - e2.isDir)
                this.sortModel['isDir'] = true;
            }
            this.clear();
            this.setData(res);
        })
        trElement.appendChild(thirdThElement);

        const t = document.createElement('th');
        t.textContent = '文件大小';
        t.addEventListener("click", () => {
            const res = Object.values(this.#dataJson);
            if (this.sortModel['size']) {
                res.sort((e1, e2) => e2.size - e1.size)
                this.sortModel['size'] = false;
            } else {
                res.sort((e1, e2) => e1.size - e2.size)
                this.sortModel['size'] = true;
            }
            this.clear();
            this.setData(res);
        })
        trElement.appendChild(t);

        const fourthThElement = document.createElement('th');
        fourthThElement.textContent = '修改时间';
        fourthThElement.addEventListener("click", () => {
            const res = Object.values(this.#dataJson);
            if (this.sortModel['lastModified']) {
                res.sort((e1, e2) => e2.lastModified - e1.lastModified)
                this.sortModel['lastModified'] = false;
            } else {
                res.sort((e1, e2) => e1.lastModified - e2.lastModified)
                this.sortModel['lastModified'] = true;
            }
            this.clear();
            this.setData(res);
        })
        trElement.appendChild(fourthThElement);

        const fifthThElement = document.createElement('th');
        fifthThElement.textContent = '操作按钮';
        trElement.appendChild(fifthThElement);

        // 将 <tr> 元素追加到 <thead> 元素中
        theadElement.appendChild(trElement);

        // 将 <thead> 元素追加到目标表格中
        this.dom.appendChild(theadElement);

        this.setData(dataList);
        this.dom.appendChild(this.dom_body);
    }

    clear(onlyStyle = false) {
        this.dom_body.innerHTML = '';
        if (onlyStyle) {
            return;
        }
        this.#dataJson = {};
        this.#checkJson = {};
        this.#length = 0;
        this.#noCheck_a_list = {};
        this.#check_a_list = {};
    }

    filter(fun) {
        const res = [];
        for (const k in this.#dataJson) {
            if (fun(this.#dataJson[k])) {
                res.push(this.#dataJson[k]);
            }
        }
        this.clear(true);
        this.setData(res);
    }

    setData(dataList) {
        for (const row of dataList) {
            this.append(row);
        }
    }

    /**
     *
     * @param dataObj {{fileName, size, lastModified,isDir}}
     */
    append(dataObj) {
        this.#dataJson[dataObj[this.#index]] = dataObj;
        // <tr className="row0">
        //     <td>
        //         <a className="click_icon okClick" href="#"></a>
        //     </td>
        //     <td>fileName1</td>
        //     <td>19Mb</td>
        //     <td>2024年4月10日 00:00:00</td>
        //     <td>
        //         <a className="okClick" href="#">删除</a>
        //         <a className="okClick" href="#">下载</a>
        //         <a className="okClick" href="#">重命名</a>
        //     </td>
        // </tr>
        const a0 = document.createElement("a");
        const f = () => {
            if (dataObj[this.#index] in this.#checkJson) {
                delete this.#checkJson[dataObj[this.#index]];
                a0.innerText = ''
                this.#noCheck_a_list[dataObj[this.#index]] = a0;
                delete this.#check_a_list[dataObj[this.#index]];
                this.#check_a_list_length--;
            } else {
                this.#checkJson[dataObj[this.#index]] = dataObj;
                a0.innerText = '';
                this.#check_a_list[dataObj[this.#index]] = a0;
                delete this.#noCheck_a_list[dataObj[this.#index]];
                this.#check_a_list_length++;
            }
        }

        const f2 = () => {
            this.trClick(dataObj);
        };

        const tr = document.createElement("tr");
        tr.className = "row" + (this.#length - (this.#length >> 1 << 1)).toString();
        const td0 = document.createElement("td");
        a0.className = "click_icon okClick";
        a0.href = "#";
        a0.innerHTML = '';
        a0.addEventListener('click', f);
        this.#noCheck_a_list[dataObj[this.#index]] = a0;
        td0.appendChild(a0);
        td0.addEventListener('click', f2);
        tr.appendChild(td0);
        const td1 = document.createElement("td");
        td1.innerText = dataObj.fileName;
        td1.addEventListener('click', f2);
        tr.appendChild(td1);
        const td2_ = document.createElement("td");
        td2_.innerText = dataObj.isDir ? " 目录" : " 文件"
        td2_.addEventListener('click', f2);
        tr.appendChild(td2_);
        const td2 = document.createElement("td");
        td2.innerText = dataObj.isDir ? '----' : DiskMirrorFront.formatBytes(dataObj.size);
        td2.addEventListener('click', f2);
        tr.appendChild(td2);
        const td3 = document.createElement("td");
        td3.innerText = DiskMirrorFront.getDate(dataObj.lastModified);
        td3.addEventListener('click', f2);
        tr.appendChild(td3);
        const td4 = document.createElement("td");
        const a40 = document.createElement("a");
        a40.className = "okClick";
        a40.href = "#";
        a40.innerText = "删除 ";
        a40.addEventListener('click', () => {
            if (this.#check_a_list_length >= 1) {
                if (confirm("您选中了多个文件，是否要批量删除？")) {
                    console.info(this.#check_a_list)
                    this.forEachChecked(this.deleteClick);
                }
            } else if (confirm("您确定要删除该文件吗？")) {
                this.deleteClick(dataObj);
            }
        });
        td4.appendChild(a40);
        const a41 = document.createElement("a");
        a41.className = "okClick";
        a41.href = "#";
        a41.innerText = "下载 ";
        a41.addEventListener('click', () => {
            this.downloadClick(dataObj);
        });
        td4.appendChild(a41);
        const a42 = document.createElement("a");
        a42.className = "okClick";
        a42.href = "#";
        a42.innerText = "重命名 ";
        a42.addEventListener('click', () => {
            this.reNameClick(dataObj);
        });
        td4.appendChild(a42);
        const a43 = document.createElement("a");
        a43.className = "okClick";
        a43.href = "#";
        a43.innerText = "预览";
        a43.addEventListener('click', () => {
            if (dataObj.isDir) {
                td0.click();
            } else {
                this.previewClick(dataObj);
            }
        });
        td4.appendChild(a43);
        tr.appendChild(td4);
        this.dom_body.appendChild(tr);
        this.#length++;
    }

    forEachChecked(fun) {
        for (const k in this.#checkJson) {
            fun(this.#checkJson[k])
        }
    }
}


class FS_List {

    backPath = [];

    #path = '/';

    #allPathFs;

    #nowPathFs;

    constructor(index_key, dataList, dom, downloadClick, deleteClick, reNameClick, pathInput, previewClick) {
        this.#allPathFs = dataList;
        this.#nowPathFs = dataList;
        this.checkBoxPoint = new Checkbox(index_key, this.#nowPathFs, dom,
            (f) => {
                const fileName = f.fileName;
                f.fileName = this.#path + f.fileName;
                downloadClick(f)
                f.fileName = fileName;
            },
            (f) => {
                const fileName = f.fileName;
                f.fileName = this.#path + f.fileName;
                deleteClick(f);
                f.fileName = fileName;
            },
            (f) => {
                f.fileName = this.#path + f.fileName;
                reNameClick(f);
            },
            f => {
                if (f.isDir) {
                    // 是目录就可以进行递归进入
                    const p = this.#path + f.fileName + '/';
                    this.setPath(p);
                }
            },
            previewClick);
        if (pathInput !== undefined) {
            this.pathInput = pathInput;
            let fsList = this;
            pathInput.addEventListener("blur", function (e) {
                fsList.setPath(e.target.value);
            });
        }
    }

    setPath(path, mark = true) {
        if (mark) {
            this.backPath.push(this.#path);
        }
        this.pathInput.value = path;
        this.#path = path;
        // 首先获取到指针
        let now = this.#allPathFs;
        // 如果路径可能发生了变化 就直接获取到路径
        const paths = path.split("/");
        // 开始进行路径的切换
        for (const p of paths) {
            if (p.length !== 0) {
                // 在这里代表 p 是有数据的 直接查找当前的层是否有这个名字的对象
                let isFind = false;
                for (const i of now) {
                    if (i.fileName === p) {
                        now = i.urls
                        isFind = true;
                        if (!i.isDir) {
                            // 代表不是一个目录 而是文件 在这里我们不进行更新了
                            window.open(i.url)
                            return;
                        }
                        break;
                    }
                }
                if (!isFind) {
                    now = undefined;
                } else if (now === undefined) {
                    break;
                }
            }
        }
        // 找到对应的路径了 直接更新数据
        this.#nowPathFs = now;
        this.checkBoxPoint.clear()
        this.checkBoxPoint.setData(now);
    }

    toBackPath() {
        if (this.backPath.length !== 0) {
            this.setPath(this.backPath.pop(), false);
            this.pathInput.value = this.#path;
        } else {
            this.setPath(this.#path.split('/').slice(0, -1).join('/'), false)
        }
    }

    filter(fun) {
        this.checkBoxPoint.filter(fun);
    }
}