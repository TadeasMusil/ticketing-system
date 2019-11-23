var config = (window.config = {}),
  $ref = $("#ref");
function animate(e) {
  var o = "animated " + e.name;
  $(e.selector)
    .addClass(o)
    .one(
      "webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend",
      function() {
        $(this).removeClass(o);
      }
    );
}
(config.ResponsiveBootstrapToolkitVisibilityDivs = {
  xs: $('<div class="device-xs \t\t\t\t  hidden-sm-up"></div>'),
  sm: $('<div class="device-sm hidden-xs-down hidden-md-up"></div>'),
  md: $('<div class="device-md hidden-sm-down hidden-lg-up"></div>'),
  lg: $('<div class="device-lg hidden-md-down hidden-xl-up"></div>'),
  xl: $('<div class="device-xl hidden-lg-down\t\t\t  "></div>')
}),
  ResponsiveBootstrapToolkit.use(
    "Custom",
    config.ResponsiveBootstrapToolkitVisibilityDivs
  ),
  (config.validations = {
    debug: !0,
    errorClass: "has-error",
    validClass: "success",
    errorElement: "span",
    highlight: function(e, o, t) {
      $(e)
        .parents("div.form-group")
        .addClass(o)
        .removeClass(t);
    },
    unhighlight: function(e, o, t) {
      $(e)
        .parents(".has-error")
        .removeClass(o)
        .addClass(t);
    },
    submitHandler: function(e) {
      e.submit();
    }
  }),
  (config.delayTime = 50),
  (config.chart = {}),
  (config.chart.colorPrimary = tinycolor(
    $ref.find(".chart .color-primary").css("color")
  )),
  (config.chart.colorSecondary = tinycolor(
    $ref.find(".chart .color-secondary").css("color")
  )),
  $(function() {
    animate({ name: "flipInY", selector: ".error-card > .error-title-block" }),
      setTimeout(function() {
        var e = $(".error-card > .error-container");
        animate({ name: "fadeInUp", selector: e }), e.addClass("visible");
      }, 1e3);
  }),
  $(function() {
    if (!$("#login-form").length) return !1;
    var e = {
      rules: {
        username: { required: !0, email: !0 },
        password: "required",
        agree: "required"
      },
      messages: {
        username: {
          required: "Please enter username",
          email: "Please enter a valid email address"
        },
        password: "Please enter password",
        agree: "Please accept our policy"
      },
      invalidHandler: function() {
        animate({ name: "shake", selector: ".auth-container > .card" });
      }
    };
    $.extend(e, config.validations), $("#login-form").validate(e);
  }),
  $(function() {
    if (!$("#reset-form").length) return !1;
    var e = {
      rules: { email1: { required: !0, email: !0 } },
      messages: {
        email1: {
          required: "Please enter email address",
          email: "Please enter a valid email address"
        }
      },
      invalidHandler: function() {
        animate({ name: "shake", selector: ".auth-container > .card" });
      }
    };
    $.extend(e, config.validations), $("#reset-form").validate(e);
  }),
 
  $(function() {
    var t = $(".item-actions-dropdown");
    $(document).on("click", function(e) {
      $(e.target).closest(".item-actions-dropdown").length ||
        t.removeClass("active");
    }),
      $(".item-actions-toggle-btn").on("click", function(e) {
        e.preventDefault();
        var o = $(this).closest(".item-actions-dropdown");
        t.not(o).removeClass("active"), o.toggleClass("active");
      });
  });
var npSettings = { easing: "ease", speed: 500 };
function setSameHeights(e) {
  e = e || $(".sameheight-container");
  var t = ResponsiveBootstrapToolkit.current();
  e.each(function() {
    var e = $(this).find(".sameheight-item"),
      o = 0;
    e.each(function() {
      $(this).css({ height: "auto" }), (o = Math.max(o, $(this).innerHeight()));
    }),
      e.each(function() {
        -1 === ($(this).data("exclude") || "").split(",").indexOf(t) &&
          $(this).innerHeight(o);
      });
  });
}
NProgress.configure(npSettings),
  $(function() {
    var e;
    setSameHeights(),
      $(window).resize(function() {
        clearTimeout(e), (e = setTimeout(setSameHeights, 150));
      });
  }),
  
  $(function() {
    var r = $("#dashboard-sales-map");
    if (!r.length) return !1;
    function e() {
      r.empty();
      var e = config.chart.colorPrimary.toHexString(),
        o = tinycolor(config.chart.colorPrimary.toString())
          .darken(40)
          .toHexString(),
        t = tinycolor(config.chart.colorPrimary.toString())
          .darken(10)
          .toHexString();
      r.vectorMap({
        map: "world_en",
        backgroundColor: "transparent",
        color: "#E5E3E5",
        hoverOpacity: 0.7,
        selectedColor: t,
        enableZoom: !0,
        showTooltip: !0,
        values: {
          us: 2e3,
          ru: 2e3,
          gb: 1e4,
          fr: 1e4,
          de: 1e4,
          cn: 1e4,
          in: 1e4,
          sa: 1e4,
          ca: 1e4,
          br: 5e3,
          au: 5e3
        },
        scaleColors: [e, o],
        normalizeFunction: "linear"
      });
    }
    e(),
      $(document).on("themechange", function() {
        e();
      });
  }),
  $(function() {
    $(".actions-list > li").on("click", ".check", function(e) {
      e.preventDefault(),
        $(this)
          .parents(".tasks-item")
          .find(".checkbox")
          .prop("checked", !0),
        removeActionList();
    });
  }),
  $(function() {
    if (!$(".form-control").length) return !1;
    $(".form-control").focus(function() {
      $(this)
        .siblings(".input-group-addon")
        .addClass("focus");
    }),
      $(".form-control").blur(function() {
        $(this)
          .siblings(".input-group-addon")
          .removeClass("focus");
      });
  }),
  $(function() {
    new Sortable($(".images-container").get(0), {
      animation: 150,
      handle: ".control-btn.move",
      draggable: ".image-container",
      onMove: function(e) {
        if ($(e.related).hasClass("add-image")) return !1;
      }
    });
    ($controlsButtons = $(".controls")),
      ($controlsButtonsStar = $controlsButtons.find(".star")),
      ($controlsButtonsRemove = $controlsButtons.find(".remove")),
      $controlsButtonsStar.on("click", function(e) {
        e.preventDefault(),
          $controlsButtonsStar.removeClass("active"),
          $controlsButtonsStar.parents(".image-container").removeClass("main"),
          $(this).addClass("active"),
          $(this)
            .parents(".image-container")
            .addClass("main");
      });
  }),
  $(function() {
    if (!$("#select-all-items").length) return !1;
    function e() {
      $(".items-list-page .sparkline").each(function() {
        for (var e = $(this).data("type"), o = [], t = 0; t < 17; t++)
          o.push(Math.round(100 * Math.random()));
        $(this).sparkline(o, {
          barColor: config.chart.colorPrimary.toString(),
          height: $(this).height(),
          type: e
        });
      });
    }
    $("#select-all-items").on("change", function() {
      var e = $(this)
        .children(":checkbox")
        .get(0);
      $(this)
        .parents("li")
        .siblings()
        .find(":checkbox")
        .prop("checked", e.checked)
        .val(e.checked)
        .change();
    }),
      e(),
      $(document).on("themechange", function() {
        e();
      });
  }),
  $(function() {
    $(".wyswyg").each(function() {
      var e = $(this).find(".editor"),
        o = $(this).find(".toolbar");
      new Quill(e.get(0), { theme: "snow", modules: { toolbar: o.get(0) } });
    });
  }),
  $(function() {
    if (
      ($("#sidebar-menu, #customize-menu").metisMenu({ activeClass: "open" }),
      $("#sidebar-collapse-btn").on("click", function(e) {
        e.preventDefault(), $("#app").toggleClass("sidebar-open");
      }),
      $("#sidebar-overlay").on("click", function() {
        $("#app").removeClass("sidebar-open");
      }),
      $.browser.mobile)
    ) {
      var e = $("#app ");
      $("#sidebar-mobile-menu-handle ").swipe({
        swipeLeft: function() {
          e.hasClass("sidebar-open") && e.removeClass("sidebar-open");
        },
        swipeRight: function() {
          e.hasClass("sidebar-open") || e.addClass("sidebar-open");
        },
        triggerOnTouchEnd: !1
      });
    }
  });
var modalMedia = {
  $el: $("#modal-media"),
  result: {},
  options: {},
  open: function(e) {
    (e = e || {}), (this.options = e), this.$el.modal("show");
  },
  close: function() {
    $.isFunction(this.options.beforeClose) &&
      this.options.beforeClose(this.result),
      this.$el.modal("hide"),
      $.isFunction(this.options.afterClose) &&
        this.options.beforeClose(this.result);
  }
};
$(function() {
  var e,
    t =
      (((e = localStorage.getItem("themeSettings")
        ? JSON.parse(localStorage.getItem("themeSettings"))
        : {}).headerPosition = e.headerPosition || ""),
      (e.sidebarPosition = e.sidebarPosition || ""),
      (e.footerPosition = e.footerPosition || ""),
      e),
    o = $("#app"),
    r = $("#theme-style"),
    a = $("#customize-menu"),
    i = a.find(".color-item"),
    n = a.find(".radio");
  function s() {
    (function() {
      t.themeName
        ? r.attr("href", "css/app-" + t.themeName + ".css")
        : r.attr("href", "css/app.css");
      return (
        o.removeClass("header-fixed footer-fixed sidebar-fixed"),
        o.addClass(t.headerPosition),
        o.addClass(t.footerPosition),
        o.addClass(t.sidebarPosition),
        o
      );
    })()
      .delay(config.delayTime)
      .queue(function(e) {
        (config.chart.colorPrimary = tinycolor(
          $ref.find(".chart .color-primary").css("color")
        )),
          (config.chart.colorSecondary = tinycolor(
            $ref.find(".chart .color-secondary").css("color")
          )),
          i.each(function() {
            $(this).data("theme") === t.themeName
              ? $(this).addClass("active")
              : $(this).removeClass("active");
          }),
          n.each(function() {
            var e = $(this).prop("name"),
              o = $(this).val();
            t[e] === o
              ? $(this).prop("checked", !0)
              : $(this).prop("checked", !1);
          }),
          localStorage.setItem("themeSettings", JSON.stringify(t)),
          $(document).trigger("themechange"),
          e();
      });
  }
  s(),
    i.on("click", function() {
      (t.themeName = $(this).data("theme")), s();
    }),
    n.on("click", function() {
      var e = $(this).prop("name"),
        o = $(this).val();
      (t[e] = o), s();
    });
}),
  $(function() {
    $("body").addClass("loaded");
  }),
  NProgress.start(),
  NProgress.done();
