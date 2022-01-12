package net.waterfallflower.cursedinterpolatorplugin.interpolator;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import net.glasslauncher.cursedinterpolator.objects.GithubCommit;
import net.waterfallflower.cursedinterpolatorplugin.interpolator.table.MappingsViewerToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CursedInterpolatorWindowFactory implements ToolWindowFactory, DumbAware {

    public static List<GithubCommit> CURRENT_COMMIT_LIST = new ArrayList<>();

    public static MappingsViewerToolWindow GUI;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        GUI = new MappingsViewerToolWindow(toolWindow, project);
        toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent((JComponent) GUI.getContentPane(), "", false));
    }
}
