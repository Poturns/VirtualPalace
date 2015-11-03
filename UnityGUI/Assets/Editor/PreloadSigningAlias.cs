using UnityEditor;

[InitializeOnLoad]
public class PreloadSigningAlias
{
    static PreloadSigningAlias()
    {
        string virtualpalace = "virtualpalace";
        PlayerSettings.Android.keystorePass = virtualpalace;
        PlayerSettings.Android.keyaliasName = virtualpalace;
        PlayerSettings.Android.keyaliasPass = virtualpalace;
    }
}