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
    static search_Params(paramName) {
        // 创建一个 URLSearchParams 对象
        const params = new URLSearchParams(window.location.search);
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
}