package net.glasslauncher.cursedinterpolator.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.waterfallflower.cursedinterpolatorplugin.api.utils.InitAndApply;
import net.waterfallflower.cursedinterpolatorplugin.api.utils.TwoValuesWithByte;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class GithubCommit {

    public String sha;

    public static @NotNull TwoValuesWithByte<List<GithubCommit>, IOException> getCommits(@NotNull String repo) {
        try(InputStream inputStream = new URL("https://api.github.com/repos/" + repo + "/commits").openStream()) {
            return InitAndApply.get(new TwoValuesWithByte<>(1, new Gson().fromJson(new InputStreamReader(inputStream), new TypeToken<List<GithubCommit>>() {}.getType()), null), value -> value.getFirstValue().forEach(commit -> commit.sha = commit.sha.substring(0, 7)));
        } catch (IOException e) {
            return new TwoValuesWithByte<>(0, null, e);
        }
    }
}
