class Utils_io {

    /**
     * 灏嗕竴涓� url 瑙ｆ瀽涓哄浘鐗� 骞跺皢涓嬭浇鎿嶄綔缁戝畾鍒颁竴涓厓绱犱腑锛岄€氬父鎴戜滑浼氱敤浜庤繘琛屾煇浜涚壒瀹氬唴瀹圭殑涓嬭浇鎿嶄綔
     * @param element{Element[]|NodeListOf<Element>} 闇€瑕佽鍋氫负鐐瑰嚮鍗充笅杞界殑鍏冪礌 濡傛灉鏄� a 鏍囩锛屽垯浼氱洿鎺ュ皢姝ゅ厓绱犲仛涓轰笅杞界殑鍏冪礌 鑰屼笉浼氶澶栧垱寤猴紒
     * @param url{string} 鍏冪礌鐨� url
     * @param filename{string} 鏂囦欢鍚�
     * @param jokerPopup{JokerBox_popUp} 涓嬭浇瀹屾瘯鎴栦笅杞藉嚭閿欑殑鐘舵€佸脊绐�
     * @param mobileDownloadFun  濡傛灉妫€娴嬪埌 鏄� 绉诲姩绔笅杞斤紝鍒欏彲鑳� base 鐨勬柟寮忎笉琚敮鎸侊紝浼氫紭鍏堣皟鐢ㄦ鎿嶄綔锛岃繖閲屽皢杩斿洖涓€涓� 鍖呭惈 url 鐨� a 鏍囩 鍜� url 瀵瑰簲鏂囦欢鐨� blob 瀵硅薄
     */
    static downloadImageDataAsFile(element,
                                   url,
                                   filename,
                                   jokerPopup,
                                   mobileDownloadFun = (a, _) => a.click()) {
        // 瑙ｆ瀽Data URL
        const parts = url.split(',');
        if (parts.length !== 2) {
            console.error('Invalid data URL', url);
            jokerPopup.show("鏃犳晥鐨勪笅杞介摼鎺�");
            return;
        }
        const base64Data = parts[1];

        // 妫€鏌ase64鏁版嵁鏄惁鏈夋晥
        if (base64Data.length === 0) {
            console.error('Empty Base64 data');
            jokerPopup.show("涓嬭浇閾炬帴涓虹┖");
            return;
        }

        try {
            const byteString = atob(base64Data);
            const arrayBuffer = new ArrayBuffer(byteString.length);
            const uint8Array = new Uint8Array(arrayBuffer);
            for (let i = 0; i < byteString.length; i++) {
                uint8Array[i] = byteString.charCodeAt(i);
            }

            // 鍋囪鍥剧墖鏄疛PEG鏍煎紡鐨勶紝鎮ㄥ彲鑳介渶瑕佹牴鎹疄闄呮儏鍐典慨鏀硅繖涓狹IME绫诲瀷
            const blob = new Blob([uint8Array], {type: 'image/png'});

            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;

            for (let htmlElement of element) {
                if (htmlElement.tagName === 'A') {
                    htmlElement.href = link.href;
                    htmlElement.download = link.download;
                    continue;
                }
                htmlElement.addEventListener('click', function (e) {
                    try {
                        if (isMobileDevice() && mobileDownloadFun) {
                            mobileDownloadFun(link, blob);
                            jokerPopup.show("寮€濮嬩笅杞藉暒!")
                            return;
                        }
                        link.click();
                        jokerPopup.show(filename + " 寮€濮嬩笅杞�...")
                    } catch (e) {
                        jokerPopup.show("涓嬭浇澶辫触锛屾偍鍙互灏濊瘯闀挎寜/鍙抽敭鍥剧墖涓嬭浇!!");
                    }
                });
            }
        } catch (error) {
            console.error('Error decoding Base64 data:', error);
            jokerPopup.show("涓嬭浇澶辫触");
        }
    }

    /**
     *
     * @param fileObject 闇€瑕佽瑙ｆ瀽鐨勬枃浠跺璞�
     * @param handleFile 鏂囦欢瀵硅薄鐨勫鐞嗗嚱鏁� 杈撳叆鐨勬槸 褰撳墠鏂囦欢鏁伴噺 鏂囦欢瀵硅薄 鍜� 鏂囦欢璺緞  浠ュ強涓€涓爣璁颁綅 褰撴偍鍦ㄥ洖璋冨嚱鏁颁腑灏嗘爣璁颁綅鐨�0绱㈠紩鍊艰缃负 true 鎵嶄細缁х画鎵ц涓嬩竴涓�
     * @param endCallback 瑙ｅ帇瀹屾瘯鐨勫洖璋冨嚱鏁� 杈撳叆鐨勬槸涓婁紶瀹屾瘯鐨勬枃浠跺璞�
     * @returns {Promise<void>} 鏃犺繑鍥�
     */
    static async unzipFile(fileObject, handleFile, endCallback = (f) => {
    }) {
        // Create a new JSZip instance
        const zip = new JSZip();

        // 杩涜涓€涓爣璁� 浠ｈ〃涓婁竴涓鐞嗘槸鍚﹀畬姣�
        let backOk = [true];
        let length = 0;

        let c = 0;
        let layer_c = 0;

        // Load the zip file from the provided file object
        zip.loadAsync(fileObject).then(
            d => {
                {
                    for (const fileName in d.files) {
                        length++;
                    }
                }
                console.info(length)
                traverseDirectory(d, '', endCallback);
            }
        )


        /**
         * 閬嶅巻鐩綍
         * @param directory
         * @param pathPrefix
         * @param endCallback {function}
         */
        function traverseDirectory(directory, pathPrefix = '', endCallback = null) {

            for (const fileName in directory.files) {
                const filePath = `${pathPrefix}/${fileName}`;
                const entry = directory.files[fileName];

                if (entry.dir) { // If it's a directory, traverse further
                    traverseDirectory(entry, filePath);
                    ++layer_c;
                } else { // If it's a file, pass the file and path to the handler function
                    entry.async('blob').then(
                        blob => {
                            // 濡傛灉涓婁竴涓枃浠�/鏂囦欢鐩綍娌℃湁澶勭悊瀹屾瘯 灏卞湪杩欓噷绛変竴浼�
                            const timeout = setInterval(() => {
                                if (backOk[0]) {
                                    // 褰撲笂涓€涓鐞嗗畬姣曚簡鍐嶅鐞嗗綋鍓嶇殑
                                    backOk[0] = false;
                                    handleFile(++c, blob, filePath, backOk);
                                    clearInterval(timeout);
                                    if (endCallback) {
                                        ++layer_c;
                                    }
                                }
                            }, 1000);
                        }
                    ).catch(
                        error => {
                            console.error(`Error processing file ${filePath}:`, error)
                        }
                    )
                }
            }

            if (endCallback !== null) {
                // 璁剧疆涓€涓畾鏃跺櫒 鍒ゆ柇褰撳墠鏄惁宸茬粡澶勭悊瀹屾瘯
                const interval = setInterval(() => {
                    if (layer_c === length) {
                        endCallback(fileObject);
                        clearInterval(interval)
                    }
                }, 1000);
            }
        }
    }
}