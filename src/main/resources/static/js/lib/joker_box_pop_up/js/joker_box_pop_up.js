/**
 * JokerBox 寮圭獥缁勪欢锛岃繖涓粍浠跺彲浠ヨ嚜鍔ㄥ疄鐜板脊绐楃殑鍔熻兘锛屼笖鍏佽鎮ㄨ嚜瀹氫箟寮圭獥鐨勬牸寮忥紒
 *
 * JokerBox pop-up component, this component can automatically implement the pop-up function and allow you to customize the format of the pop-up!
 */
class JokerBox_popUp {

    /**
     * 灏嗗脊绐楀姛鑳借杞藉埌涓€涓厓绱犱笂锛岃繖灏嗕細鎶婂脊绐楃粍浠剁殑澹版槑鍛ㄦ湡涓庡厓绱犵殑鐢熷懡鍛ㄦ湡缁戝畾鍦ㄤ竴璧�
     *
     * Load the pop-up function onto an element, which will bind the declaration cycle of the pop-up component to the lifecycle of the element
     * @param dom {HTMLElement} 闇€瑕佹壙杞藉脊绐楃殑鍏冪礌
     *
     * Elements that need to carry pop-ups
     *
     * @param cssFormatter {string} 鍦ㄥ脊绐椾腑鏄剧ず鍐呭鏃惰浣跨敤鐨勯鍒舵牸寮忥紝鎴戜滑鍏佽骞舵敮鎸佹偍浣跨敤鑷繁鐨勬牸寮忓仛涓哄脊绐椾腑鐨勫睍绀猴紝璇锋敞鎰忥紝杩欏皢浼氳鎮ㄧ殑鍏冪礌琚� jokerBox 绠＄悊锛�
     * We allow and support you to use your own pre made format for displaying content in pop ups. Please note that this will allow your elements to be managed by jokerBox!
     *
     * Elements that need to carry pop-ups
     * @param color {{color: string, background: string}} 鍒嗗埆鏄儗鏅鑹插拰瀛椾綋棰滆壊
     *
     *
     * They are background color and font color, respectively
     * @param closeClickAnimation 鍏抽棴鎸夐挳琚偣鍑讳箣鍚庣殑鍏抽棴鍔ㄧ敾鏁堟灉 鍦ㄨ繖閲屼娇鐢ㄧ殑鏄唴缃殑鍔ㄧ敾锛屽鏋滄偍鏈夎嚜宸辩殑鍔ㄧ敾锛屽彲浠ョ洿鎺ュ湪杩欓噷浼犻€掑姩鐢荤殑鍚嶅瓧锛�
     *
     * The close animation effect after the close button is clicked here uses the built-in animation. If you have your own animation, you can directly pass the name of the animation here!
     */
    constructor(dom,
                color = {},
                closeClickAnimation = 'jokerBox_pop_up_show_display_life_close',
                cssFormatter = undefined) {

        console.info(`jokerBox_v${JokerBox_popUp.getVersion()} -> popUp set ok!!`)

        // 鍒ゆ柇鑳屾櫙棰滆壊鏄惁涓� 绌�
        if (color['background'] === undefined) {
            color['background'] = '#ccf8bf';
        }

        // 鍒ゆ柇瀛椾綋棰滆壊鏄惁涓虹┖
        if (color['color'] === undefined) {
            color['color'] = JokerBoxUtils.invertColor(color['background']);
        }

        let div1, div2, div3;

        if (cssFormatter !== undefined) {
            div1 = document.querySelector(cssFormatter);
            const s1 = cssFormatter + ' .pop_up_content_text';
            const s2 = cssFormatter + ' .pop_up_content_close';
            div2 = document.querySelector(s1);
            div3 = document.querySelector(s2);
            if (!(div1 && div2 && div3)) {
                console.error("jokerBox_popUp: subHtml is undefined!!\n" +
                    "You need to ensure that the following two class name elements are included in the format you specify\n" +
                    "\n" +
                    "1. " + s1 + ": Text content display element锛圱here can only be one锛塡n" +
                    "\n" +
                    "2. " + s2 + ": Close button display element锛圱here can only be one锛�", {
                    "your pop_up_content_text": div2,
                    "your pop_up_content_close": div3
                });
                return;
            }
        } else {
            div1 = document.createElement("div");
            div2 = document.createElement("div");
            div3 = document.createElement("a");
            div1.classList.add('jokerBox_pop_up_show_div')
            div2.classList.add('pop_up_content_text');
            div3.classList.add('pop_up_content_close');

            div1.appendChild(div3);
            div1.appendChild(div2);
            dom.appendChild(div1);
        }

        div2.innerText = 'welcome to use jokerBox!!!';
        div3.innerText = 'x'
        div3.title = '鍏抽棴寮圭獥'
        div3.href = '#'
        div3.onclick = () => {
            div1.style.animation = closeClickAnimation + ' 1s forwards';
        }


        this.dom = div1;
        this.textDom = div2;

        this.dom.style.background = color['background'];
        this.dom.style.transform = 'scale(0)';
        this.dom.style.height = '0';
        for (let childNode of this.dom.childNodes) {
            if (childNode.style === undefined) {
                continue;
            }
            childNode.style.color = color['color'];
        }

        const invertColor1 = JokerBoxUtils.invertColor(color['color']);
        div3.onmouseover = function () {
            this.style.color = invertColor1;
            this.style.transform = 'scale(150%)';
            this.style.textShadow = '20% 20% 20% 20% #fff';
        }
        div3.onmouseout = function () {
            this.style.color = color['color'];
            this.style.transform = 'scale(100%)';
        }

        this.dom.onanimationend = function () {
            this.style.animation = 'none';
        }
    }

    /**
     * 灏嗗脊绐楀姛鑳借杞藉埌涓€涓厓绱犱笂锛岃繖灏嗕細鎶婂脊绐楃粍浠剁殑澹版槑鍛ㄦ湡涓庡厓绱犵殑鐢熷懡鍛ㄦ湡缁戝畾鍦ㄤ竴璧�
     *
     * Load the pop-up function onto an element, which will bind the declaration cycle of the pop-up component to the lifecycle of the element
     * @param dom {HTMLElement} 闇€瑕佹壙杞藉脊绐楃殑鍏冪礌
     *
     *
     * @param cssFormatter {string} 鍦ㄥ脊绐椾腑鏄剧ず鍐呭鏃惰浣跨敤鐨勯鍒舵牸寮忥紝鎴戜滑鍏佽骞舵敮鎸佹偍浣跨敤鑷繁鐨勬牸寮忓仛涓哄脊绐椾腑鐨勫睍绀猴紝璇锋敞鎰忥紝杩欏皢浼氳鎮ㄧ殑鍏冪礌琚� jokerBox 绠＄悊锛�
     * We allow and support you to use your own pre made format for displaying content in pop ups. Please note that this will allow your elements to be managed by jokerBox!
     *
     * Elements that need to carry pop-ups
     * @param color {{color: string, background: string}} 鍒嗗埆鏄儗鏅鑹插拰瀛椾綋棰滆壊
     *
     *
     * They are background color and font color, respectively
     * @param closeClickAnimation 鍏抽棴鎸夐挳琚偣鍑讳箣鍚庣殑鍏抽棴鍔ㄧ敾鏁堟灉 鍦ㄨ繖閲屼娇鐢ㄧ殑鏄唴缃殑鍔ㄧ敾锛屽鏋滄偍鏈夎嚜宸辩殑鍔ㄧ敾锛屽彲浠ョ洿鎺ュ湪杩欓噷浼犻€掑姩鐢荤殑鍚嶅瓧锛�
     *
     * The close animation effect after the close button is clicked here uses the built-in animation. If you have your own animation, you can directly pass the name of the animation here!
     */
    static createByHtml(dom, cssFormatter,
                        color = {},
                        closeClickAnimation = 'jokerBox_pop_up_show_display_life_close') {
        return new JokerBox_popUp(dom, color, closeClickAnimation, cssFormatter);
    }

    /**
     * 灏嗗脊绐楀姛鑳借杞藉埌涓€涓厓绱犱笂锛岃繖灏嗕細鎶婂脊绐楃粍浠剁殑澹版槑鍛ㄦ湡涓庡厓绱犵殑鐢熷懡鍛ㄦ湡缁戝畾鍦ㄤ竴璧�
     *
     * Load the pop-up function onto an element, which will bind the declaration cycle of the pop-up component to the lifecycle of the element
     * @param dom {HTMLElement} 闇€瑕佹壙杞藉脊绐楃殑鍏冪礌
     *
     * Elements that need to carry pop-ups
     * @param color {{color: string, background: string}} 鍒嗗埆鏄儗鏅鑹插拰瀛椾綋棰滆壊
     *
     * They are background color and font color, respectively
     * @param closeClickAnimation 鍏抽棴鎸夐挳琚偣鍑讳箣鍚庣殑鍏抽棴鍔ㄧ敾鏁堟灉 鍦ㄨ繖閲屼娇鐢ㄧ殑鏄唴缃殑鍔ㄧ敾锛屽鏋滄偍鏈夎嚜宸辩殑鍔ㄧ敾锛屽彲浠ョ洿鎺ュ湪杩欓噷浼犻€掑姩鐢荤殑鍚嶅瓧锛�
     *
     * The close animation effect after the close button is clicked here uses the built-in animation. If you have your own animation, you can directly pass the name of the animation here!
     */
    static create(dom,
                  color = {},
                  closeClickAnimation = 'jokerBox_pop_up_show_display_life_close') {
        return this.createByHtml(dom, undefined, color, closeClickAnimation)
    }

    static getVersion() {
        return '1.0.0';
    }

    /**
     * 寮圭獥鏄剧ず妗�
     * @param msg {string} 闇€瑕佸湪寮圭獥涓樉绀虹殑鍐呭
     * @param timeMs {number} 寮圭獥鍔ㄧ敾+鏄剧ず鐨勬€绘椂闂� 姣鏁板€�
     */
    show(msg, timeMs = 10000) {
        this.textDom.innerText = msg;
        this.dom.style.animation = `jokerBox_pop_up_show_display_life ${timeMs}ms forwards`;
    }
}

/**
 * JokerBoxUtils 灏忎笐鐩� 寮圭獥宸ュ叿鍖�
 * @class JokerBoxUtils
 * @classdesc JokerBoxUtils
 */
class JokerBoxUtils {

    /**
     * 璁＄畻涓€涓鑹叉暟鍊肩殑鍙嶈壊
     * @param color 闇€瑕佽璁＄畻鐨勯鑹叉暟鍊�
     * @return {string} 璁＄畻缁撴灉 鍙嶈壊鐨勯鑹叉暟鍊� 16 杩涘埗
     */
    static invertColor(color) {
        // 灏嗛鑹插€艰浆鎹负 RGB 鏍煎紡
        const r = parseInt(color.substring(1, 3), 16);
        const g = parseInt(color.substring(3, 5), 16);
        const b = parseInt(color.substring(5, 7), 16);

        // 璁＄畻鍙嶈壊鍊�
        const invertedR = 255 - r;
        const invertedG = 255 - g;
        const invertedB = 255 - b;

        // 灏嗗弽鑹插€艰浆鎹负鍗佸叚杩涘埗鏍煎紡锛屽苟杩斿洖缁撴灉
        return '#' + invertedR.toString(16).padStart(2, '0') + invertedG.toString(16).padStart(2, '0') + invertedB.toString(16).padStart(2, '0');
    }

}