package org.cloudfoundry.samples.music.domain;

public class ApplicationInfo {
    private String[] profiles;
    private String[] services;
    private String[] props;

    public ApplicationInfo(String[] profiles, String[] services) {
        this.profiles = profiles;
        this.services = services;
        this.props = new String[0];
    }

    public ApplicationInfo(String[] profiles, String[] services, String[] props) {
        this.profiles = profiles;
        this.services = services;
        this.props = props;
    }

    public String[] getProfiles() {
        return profiles;
    }

    public void setProfiles(String[] profiles) {
        this.profiles = profiles;
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    public String[] getProps() {
        return props;
    }

    public void setProps(String[] props) {
        this.props = props;
    }
}
