<template>
  <el-card>
    <h1 class="me-author-name">gongji's blog</h1>
    <div class="me-author-description">
      <span><i class="el-icon-location-outline"></i> &nbsp;湖南长沙</span>
      <span><i class="me-icon-job"></i> &nbsp;java实习生</span>
    </div>
    <div class="me-author-tool">
      <i @click="showTool(qq)" :title="qq.title" class="iconfont icon-qq"></i>
      <i @click="showTool(github)" :title="github.title" class="iconfont icon-github"></i>
    </div>
  </el-card>

</template>

<script>
export default {
  name: 'CardMe',
  data() {
    return {
      qq: {title: 'QQ', message: '2549829740'},
      github: {title: 'github', message: 'gongji97'},
    }
  },
  methods: {
    showTool(way) {
      if (way.title == 'QQ') {
        window.open("http://wpa.qq.com/msgrd?v=3&uin=" + way.message + "&site=qq&menu=yes/");
      } else if (way.title == 'github') {
        window.open("https://github.com/" + way.message);
      }
    },

  }
}
</script>

<style scoped>
.me-author-name {
  text-align: center;
  font-size: 30px;
  border-bottom: 1px solid #5FB878;
}

.me-author-description {
  padding: 8px 0;
}

.me-icon-job {
  padding-left: 16px;
}

.me-author-tool {
  text-align: center;
  padding-top: 10px;
}

.me-author-tool i {
  cursor: pointer;
  padding: 4px 10px;
  font-size: 30px;
}
</style>
