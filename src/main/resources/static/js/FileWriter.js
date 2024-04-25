const defData = `# 请在这里写文章标题，请记得将文章保存哦！

> 此时虽然您设置了文件的名字，但是并没有保存文件，您需要点击上方的保存按钮哦！

## 目录

[TOC]

![文章的封面](image/logo.png "文章的封面")
## 开始

在这里可以书写文章正文，请不要忘记保存您写的内容哦!!!
您可以直接将文件粘贴到文章正文中，这样会自动的进行文件上传，上传之后的文章将存储在同级目录中的 resource 文件夹中。
例如我的文件名为 \`test.md\` 因此在此文章中粘贴的附件将会自动的存储在 \`test.md_resource\` 中。

\`\`\`java
public static boolean main() {
\tSystem.out.println("您也可以在这里写一个代码块哦！")
\treturn false;
}\`\`\`
`;

function init(str) {
    // 新增状态的 初始化可见性 以及 文章数据
    return editormd("article_editor", {
        width: "100%",
        height: '92.5%',
        htmlDecode: "video,style,iframe,sub,sup|on*",
        markdown: str ? str : defData,
        // Editor.md theme, default or dark, change at v1.5.0
        // You can also custom css class .editormd-theme-xxxx
        theme: "dark",

        // Preview container theme, added v1.5.0
        // You can also custom css class .editormd-preview-theme-xxxx
        previewTheme: "dark",

        // Added @v1.5.0 & after version this is CodeMirror (editor area) theme
        editorTheme: "twilight",
        emoji: true,
        path: "editor.md/lib/"  // Autoload modules mode, codemirror, marked... dependents libs path
    });
}


