const jokerBoxPopUp = new JokerBox_popUp(document.getElementById("joker"));
jokerBoxPopUp.show("请您输入信息哦!!! 若您的信息有误则可能会导致无法访问服务器！！！")

document.getElementById("submitButton").addEventListener("click", function () {
    let value = document.getElementById("server_pass").value;

    if (document.getElementById("server_id").value === "") {
        jokerBoxPopUp.show("请您输入信息哦!!! ")
        return;
    }
    if (value === undefined) {
        value = '0';
    }
    document.cookie = `diskMirror_server_pass=${value}; path=/`;
    // 计算 token
    window.location.href = `FileExplorer.html?server_id=${document.getElementById("server_id").value}`;
})