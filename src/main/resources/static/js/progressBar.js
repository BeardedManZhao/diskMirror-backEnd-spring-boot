/*
 * @Author: 璧靛噷瀹囷紙BeardedManZhao锛� 鍒涘缓
 * progressBar-js 搴� 婧愪唬鐮侊細https://github.com/BeardedManZhao/progressBar-js
 */

/**
 * 杩涘害鏉″璞�
 */
class ProgressBar {

    /**
     * 鏋勫缓涓€涓繘搴︽潯
     * @param e 闇€瑕佽鍋氫负杩涘害鏉″鍣ㄧ殑鍏冪礌
     * @param showFun 杩涘害鏉℃樉绀� 鍑芥暟 鎺ユ敹杩涘害鐧惧垎姣旀暟鍊� 鎴栬€� 鏁板€兼瘮渚� 杩斿洖鏄剧ず鐨勬枃瀛�
     */
    constructor(e, showFun = undefined) {
        this.proElement = e;
        this.fill = document.createElement('div');
        this.fill.className = 'progress-bar-fill';
        this.proElement.appendChild(this.fill);
        this.showFun = showFun === undefined ? (p) => "褰撳墠杩涘害锛�" + p : showFun;
    }

    /**
     * 璁剧疆杩涘害鏉＄殑杩涘害棰滆壊
     * @param progress {number} 褰撳墠杩涘害
     * @param color 杩涘害鏉＄殑姝ｅ父棰滆壊
     * @param maxColor 杩涘害鏉″嵆灏嗗埌杈句笂闄愰鑹�
     */
    updateProgressColor(progress, color = undefined, maxColor = 'linear-gradient(to right, rgba(255, 255, 255, 0.5), #f00)') {
        if (progress >= 90 && maxColor !== undefined) {
            this.fill.style.background = maxColor;
        } else if (color !== undefined) {
            this.fill.style.background = color;
        }
    }

    /**
     * 璁剧疆杩涘害鏉＄殑杩涘害
     * @param progress {number}
     * @param color 杩涘害鏉＄殑姝ｅ父棰滆壊
     * @param maxColor 杩涘害鏉″嵆灏嗗埌杈句笂闄愰鑹�
     */
    setProgress(progress, color = undefined, maxColor = 'linear-gradient(to right, rgba(255, 255, 255, 0.5), #f00)') {
        this.setProgressByValue(Math.max(progress, 0), 100, color, maxColor);
    }

    /**
     * 璁剧疆杩涘害鏉＄殑杩涘害 褰撳墠鏁板€�/鏈€澶ф暟鍊�
     * @param progressValue {number} 褰撳墠鏁板€�
     * @param maxValue {number} 鏈€澶ф暟鍊�
     * @param color 杩涘害鏉＄殑姝ｅ父棰滆壊
     * @param maxColor 杩涘害鏉″嵆灏嗗埌杈句笂闄愰鑹�
     */
    setProgressByValue(progressValue, maxValue, color = undefined, maxColor = 'linear-gradient(to right, rgba(255, 255, 255, 0.5), #f00)') {
        this.proElement.title = this.showFun(progressValue, maxValue);
        const p = Math.min(progressValue, maxValue) / maxValue * 100;
        this.fill.style.width = p + '%';
        this.updateProgressColor(p, color, maxColor);
    }
}