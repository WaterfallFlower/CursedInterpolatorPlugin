package net.waterfallflower.cursedinterpolatorplugin.interpolator.settings;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import lombok.Getter;
import net.glasslauncher.cursedinterpolator.objects.GithubCommit;
import net.waterfallflower.cursedinterpolatorplugin.CursedInterpolatorSettingsStorage;
import net.waterfallflower.cursedinterpolatorplugin.api.DownloadCommitRunnable;
import net.waterfallflower.cursedinterpolatorplugin.api.IndirectUse;
import net.waterfallflower.cursedinterpolatorplugin.api.ValueWithByte;
import net.waterfallflower.cursedinterpolatorplugin.api.ui.SmallButton;
import net.waterfallflower.cursedinterpolatorplugin.interpolator.CursedInterpolatorWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class CursedInterpolatorSettingsForm {
    @Getter
    private JPanel mainPanel;
    @IndirectUse
    private JLabel MCP_LOC_LABEL;
    private TextFieldWithBrowseButton MCP_LOC_BOX;
    @IndirectUse
    private JLabel MAPPINGS_LABEL;
    @IndirectUse
    private JPanel mainSettingsLabel;
    @IndirectUse
    private JPanel mainInfo;
    private JLabel NOTE_LABEL;
    private JRadioButton USE_TINY_FILE;
    private JRadioButton USE_GITHUB_COMMIT;
    private JPanel SUB_TINY_FILE;
    private JPanel SUB_GITHUB_COMMIT;
    @IndirectUse
    private JLabel TINY_FILE_LABEL;
    private TextFieldWithBrowseButton TINY_FILE_FIELD;
    @IndirectUse
    private JLabel MCP_LOC_L2;
    @IndirectUse
    private JLabel GITHUB_COMMIT_LABEL;
    private JComboBox<String> GITHUB_COMMIT_FIELD;
    @IndirectUse
    private JPanel MCP_GENERAL;
    private JComboBox<String> COMMIT_RELOAD_LIST;
    private SmallButton COMMIT_RELOAD_BUTTON;
    private JLabel COMMIT_RELOAD_LABEL;
    private JPanel COMMIT_RELOAD_INFO;
    private JLabel COMMIT_INFO_LABEL;
    private JLabel COMMIT_INFO_VERSION_LABEL;
    private JLabel COMMIT_INFO_INSTALLED_LABEL;
    private JTextArea COMMIT_VERSION_DYNAMIC;
    private JTextArea COMMIT_INSTALLED_DYNAMIC;
    private SmallButton COMMIT_INSTALL_ACTION;
    private JLabel ERROR_LABEL;
    private SmallButton COMMIT_RELOAD_ACTION;

    public String getBoxString() {
        return MCP_LOC_BOX.getText();
    }

    public void setBoxString(@NotNull String s) {
        MCP_LOC_BOX.setText(s);
    }

    public boolean useTinyOrGithub() {
        return USE_TINY_FILE.isSelected();
    }

    public void setTinyOrGithub(boolean b) {
        if(b)
            USE_TINY_FILE.setSelected(true);
        else
            USE_GITHUB_COMMIT.setSelected(true);
    }

    public String getTinyFileLocation() {
        return TINY_FILE_FIELD.getText();
    }

    public void setTinyFileLocation(@NotNull String s) {
        TINY_FILE_FIELD.setText(s);
    }

    public String getGithubRepo() {
        return (String) GITHUB_COMMIT_FIELD.getSelectedItem();
    }

    public void setGithubRepo(@NotNull String s) {
        GITHUB_COMMIT_FIELD.addItem(s);
    }

    public String getGithubCommit() {
        return SHA_COMMIT_FULL;
    }

    public void setGithubCommit(@NotNull String s) {
        SHA_COMMIT_FULL = s;
    }


    private void checkMappingsUIStatus() {
        for(Component q : SUB_TINY_FILE.getComponents())
            if(!(q instanceof JRadioButton))
                q.setEnabled(USE_TINY_FILE.isSelected());

        for(Component q : SUB_GITHUB_COMMIT.getComponents())
            if(!(q instanceof JRadioButton))
                q.setEnabled(USE_GITHUB_COMMIT.isSelected());
    }

    public CursedInterpolatorSettingsForm() {
        MCP_LOC_BOX.addBrowseFolderListener("Select MCP Folder", "", null, FileChooserDescriptorFactory.createSingleFolderDescriptor());
        TINY_FILE_FIELD.addBrowseFolderListener("Select Tiny Mappings File", "", null, FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

        NOTE_LABEL.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Search is global and returns a set of classes that contain a match for the input. \n" +
                                "Search is case sensitive!\n\nData elements searched on:\n" +
                                "Classes:\n    ~ Pkg Name\n    ~ SRG Name\n    ~ Obf Name\n" +
                                "Methods/Fields:\n    ~ SRG Name\n    ~ Obf Name\n    ~ MCP Name\n    ~ Comment"
                        , "Search Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        NOTE_LABEL.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        NOTE_LABEL.setForeground(JBColor.BLUE);
        ERROR_LABEL.setForeground(JBColor.RED);

        USE_TINY_FILE.addItemListener(itemEvent -> checkMappingsUIStatus());
        USE_GITHUB_COMMIT.addItemListener(itemEvent -> checkMappingsUIStatus());

        COMMIT_RELOAD_BUTTON.setIcon(AllIcons.Actions.Refresh);
        COMMIT_RELOAD_BUTTON.addActionListener(actionEvent -> {
            if(GITHUB_COMMIT_FIELD.getSelectedItem() != null) {
                COMMIT_RELOAD_BUTTON.setEnabled(false);
                updateCurrentCommitList((String) GITHUB_COMMIT_FIELD.getSelectedItem());
                COMMIT_RELOAD_BUTTON.setEnabled(true);
            }
        });

        COMMIT_RELOAD_ACTION.setIcon(AllIcons.Actions.Refresh);
        COMMIT_RELOAD_ACTION.addActionListener(actionEvent -> commitReloadListCheck());

        COMMIT_INSTALL_ACTION.setIcon(AllIcons.Actions.Download);
        COMMIT_INSTALL_ACTION.addActionListener(actionEvent -> ProgressManager.getInstance().run(new DownloadCommitRunnable(
                getBoxString(),
                getGithubRepo(),
                getGithubCommit(),
                aByte -> checkMappingsLoaded()
        )));

        updateCurrentCommitList(CursedInterpolatorSettingsStorage.getInstance().MAPPINGS_INFO);

        COMMIT_RELOAD_LIST.setSelectedItem(SHA_COMMIT_FULL);
        COMMIT_RELOAD_LIST.addItemListener(itemEvent -> commitReloadListCheck());
    }

    private void commitReloadListCheck() {
        if(COMMIT_RELOAD_LIST.getSelectedItem() != null && ((String)COMMIT_RELOAD_LIST.getSelectedItem()).length() > 0) {
            SHA_COMMIT_FULL = (String) COMMIT_RELOAD_LIST.getSelectedItem();
            COMMIT_VERSION_DYNAMIC.setText(SHA_COMMIT_FULL.substring(0, 7));
            checkMappingsLoaded();
        }
    }

    private void updateCurrentCommitList(@NotNull String s) {
        if(s.length() > 0) {
            ValueWithByte<List<GithubCommit>> commits = GithubCommit.getCommits(s);

            if(commits.getByteVar() == 1) {
                CursedInterpolatorWindowFactory.CURRENT_COMMIT_LIST = commits.getValue();
                updateCommitList();
                setErrorStatus(false);
            } else {
                COMMIT_RELOAD_LIST.removeAllItems();
                setErrorStatus(true);
            }
        }
    }

    private void updateCommitList() {
        COMMIT_RELOAD_LIST.removeAllItems();
        for (GithubCommit commit : CursedInterpolatorWindowFactory.CURRENT_COMMIT_LIST)
            COMMIT_RELOAD_LIST.addItem(commit.sha);
    }

    private void setErrorStatus(boolean b) {
        ERROR_LABEL.setText(b ? "Something went wrong while updating commit list..." : "");
    }

    private void checkMappingsLoaded() {
        COMMIT_INSTALLED_DYNAMIC.setText(new File(MCP_LOC_BOX.getText(), "conf/interpolator/" + getGithubRepo().replace('/', '.') + "-" + getGithubCommit().substring(0, 7) + ".tiny").exists() ? ("true (" + CursedInterpolatorSettingsStorage.getMappings().getName() + ")") : "false");
    }

    private String SHA_COMMIT_FULL = "";
}