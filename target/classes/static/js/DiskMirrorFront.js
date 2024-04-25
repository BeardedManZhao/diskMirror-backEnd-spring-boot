class DiskMirrorFront {
    /**
     * 时间转换工具类
     * @param n 需要被转换的时间的毫秒值
     * @return {string} 转换之后的时间戳数值
     */
    static getDate(n) {
        if (n instanceof Date) {
            let y = n.getFullYear(),
                m = n.getMonth() + 1,
                d = n.getDate();
            return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + " " + n.toTimeString().substr(0, 8);
        }
        let now = new Date(n),
            y = now.getFullYear(),
            m = now.getMonth() + 1,
            d = now.getDate();
        return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + " " + now.toTimeString().substr(0, 8);
    }

    /**
     * 计算单位度量
     * @param bytes 需要被计算的 byte 单位
     * @param decimals 计算结果的精确值 小数点后的保留位数
     * @return {string}  计算结果
     */
    static formatBytes(bytes, decimals = 2) {
        if (bytes <= 0) return '0 Bytes';

        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];

        const i = Math.floor(Math.log2(bytes) / Math.log2(k));

        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    }

    /**
     *  深度拷贝json对象
     * @param jsonObj
     * @returns {{}|*}
     */
    static deepCopyJson(jsonObj) {
        if (typeof jsonObj !== 'object' || jsonObj === null) {
            // 如果不是对象或数组，直接返回原值
            return jsonObj;
        }

        if (Array.isArray(jsonObj)) {
            // 处理数组
            return jsonObj.map(item => this.deepCopyJson(item));
        }

        const copy = {};
        for (const key in jsonObj) {
            if (jsonObj.hasOwnProperty(key)) {
                copy[key] = this.deepCopyJson(jsonObj[key]);
            }
        }
        return copy;
    }

    /**
     * 搜索URL中的指定的参数
     * @return 搜索到的同名参数组合的 list
     */
    static search_Params(paramName, url = undefined) {
        // 创建一个 URLSearchParams 对象
        const params = new URLSearchParams(url ? url.substring(url.lastIndexOf('?')) : window.location.search);
        // 获取名为 "paramName" 的参数值
        const paramValues = params.getAll(paramName);
        // 将参数值封装为列表
        return Array.from(paramValues);
    }

    /**
     * 获取最新的 cookie 的值
     * @param name 需要获取的 cookie 的名称
     * @returns {null|String} 如果找到 则返回cookie的值，如果找不到则返回 null
     */
    static getLatestCookieValue(name) {
        let latestValue = null;
        const all = document.cookie.split(";");

        for (let i = 0; i < all.length; i++) {
            const cookiePair = all[i].trim().split("=");
            if (decodeURIComponent(cookiePair[0]) === name) {
                latestValue = decodeURIComponent(cookiePair[1]);
            }
        }

        return latestValue;
    }

    /**
     * 处理剪贴板中的数据列表，仅仅能够处理粘贴板中的数据列表，不能自动回调
     *
     * @param items 粘贴板中的数据列表。
     * @param callbackImage 图片回调函数。
     * @param callbackText 文本回调函数。
     * @param callbackVideo 视频回调函数。
     * @param callbackFile 文件回调函数。
     * @param useLastData 是否只获取到最新复制数据 如果选择是 则不会获取到所有的数据
     */
    static handleClipboardData(items, callbackImage, callbackText, callbackVideo, callbackFile, useLastData = true) {
        console.info("handleClipboardData run!!!")
        if (!items || items.length === 0) {
            return;
        }
        let blob;

        function run(item) {
            if (item.type.indexOf('image') !== -1) {
                // 图片数据
                blob = item.getAsFile();
                callbackImage(blob);
            } else if (item.type.indexOf('video/') === 0) {
                // 视频数据
                blob = item.getAsFile();
                callbackVideo(blob);
            } else if (item.kind === 'string') {
                // 文本数据
                item.getAsString(callbackText)
            } else if (item.kind === 'file') {
                // 其它的文件数据
                blob = item.getAsFile();
                callbackFile(blob);
            }
        }

        if (useLastData) {
            run(items[items.length - 1]);
            return;
        }
        for (let i = 0; i < items.length; i++) {
            run(items[i]);
        }
    }


    /**
     * 读取剪贴板中的最新一条数据，并根据数据类型调用不同的回调函数。
     *
     * @param e 需要被设置粘贴事件的元素列表。
     * @param {Function} callbackImage 处理图片数据的回调函数。
     * @param {Function} callbackText 处理文本数据的回调函数。除了这个函数的参数是元素和字符串 其它的都是 元素和file 对象
     * @param {Function} callbackVideo 处理视频数据的回调函数。
     * @param {Function} callbackFile 处理任意类型文件数据的回调函数。
     * @param {boolean} preventDefault 是否阻止默认事件。默认值为 false。
     * @param eventName 对应的事件对象的名称。
     * @param preventDefault 是否阻止默认事件。默认值为 false。
     * @param useLastData 是否只获取到最新复制数据 如果选择是 则不会获取到所有的数据
     */
    static readClipboard(e, callbackImage, callbackText, callbackVideo, callbackFile, eventName = 'paste', preventDefault = false, useLastData = true) {
        function hf(event) {
            if (preventDefault) {
                event.preventDefault();
            }
            const items = (event.clipboardData || window.clipboardData).items;
            DiskMirrorFront.handleClipboardData(items, callbackImage, callbackText, callbackVideo, callbackFile, useLastData);
        }

        e.forEach(function (element) {
            element.addEventListener(eventName, hf);
        });
    }

    /**
     * 获取url中的内容
     * @param url 需要被请求的 url
     * @returns {Promise<string>} 其中包含来自远程服务器的内容
     */
    static async fetchUrlAsString(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                console.error(`Request failed with status: ${response.status}`)
                return Promise.resolve("error! 请稍后再试!")
            }

            return await response.text();
        } catch (error) {
            throw new Error('Unable to complete the request');
        }
    }

    /**
     * 生成 video 标签
     * @param src 视频地址
     * @param style 视频样式
     * @return {string} 视频标签
     */
    static videoStr(src, style = 'width:50vw') {
        return "<video src='" + src + "' style='" + style + "' controls/>";
    }
}