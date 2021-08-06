package de.polocloud.webinterface.handler;

import de.polocloud.webinterface.WebInterfaceModule;
import de.polocloud.webinterface.user.WebUser;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader;
import org.jetbrains.annotations.NotNull;

public abstract class PageHandler implements Handler {

    private Template template;
    private WebUser webUser;

    protected static final TemplateLoader loader = new TemplateLoader.ClasspathTemplateLoader();

    public PageHandler(Template template) {
        this.template = template;
    }

    public abstract void populateTemplateContext(Context context, TemplateContext templateContext);

    public WebUser getWebUser() {
        return webUser;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        TemplateContext templateContext = new TemplateContext();

        populateTemplateContext(ctx, templateContext);

        String sessionID = ctx.cookie("sessionID");

        if (sessionID == null) {
            webUser = null;
            System.out.println(1);
        } else {
            System.out.println(2);

            WebUser webUser = WebInterfaceModule.getInstance().getWebUserManager().getUserBySessionID(sessionID);
            if (webUser == null) {
                System.out.println(3);
                this.webUser = null;
            } else {
                System.out.println(4);
                this.webUser = webUser;
            }
        }

        if (this.webUser == null) {
            System.out.println(5);

            if (!isLoginPage()) {
                System.out.println(6);

                ctx.redirect("/");
                return;
            }
        }else{
            if(isLoginPage()){
                ctx.redirect("/dashboard");
                System.out.println(8);
                return;
            }
        }
        System.out.println(7);

        String render = template.render(templateContext);
        ctx.status(200);
        ctx.html(render);
    }

    public abstract boolean isLoginPage();

}
