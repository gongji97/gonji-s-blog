<template>
  <div v-title data-title="gongji">
    <el-container>

      <el-main class="me-articles">

        <article-scroll-page></article-scroll-page>

      </el-main>

      <el-aside>

        <card-me class="me-area"></card-me>
        <card-tag :tags="hotTags"></card-tag>

        <card-article cardHeader="最热文章" :articles="hotArticles"></card-article>

        <card-archive cardHeader="文章归档" :archives="archives"></card-archive>

        <card-article cardHeader="最新文章" :articles="newArticles"></card-article>

      </el-aside>

    </el-container>

    <!-- 准备一个容器用来存放音乐播放器 -->
    <div id="aplayer"></div>

  </div>
</template>

<script>
import CardMe from '@/components/card/CardMe'
import CardArticle from '@/components/card/CardArticle'
import CardArchive from '@/components/card/CardArchive'
import CardTag from '@/components/card/CardTag'
import ArticleScrollPage from '@/views/common/ArticleScrollPage'

import {getArticles, getHotArtices, getNewArtices} from '@/api/article'
import {getHotTags} from '@/api/tag'
import {listArchives} from '@/api/article'

import APlayer from "APlayer"; // 引入音乐插件
import "APlayer/dist/APlayer.min.css"; // 引入音乐插件的样式


export default {
  name: 'Index',
  created() {

    this.getHotArtices()
    this.getNewArtices()
    this.getHotTags()
    this.listArchives()
  },
  data() {
    return {
      hotTags: [],
      hotArticles: [],
      newArticles: [],
      archives: [],
      audio: [ // 歌曲列表
        {
          name: "喜帖街", // 歌曲名字
          artist: "谢安琪", // 歌曲演唱者
          url: // 歌曲地址（这里用外链地址）
            "https://m10.music.126.net/20220307122821/3dce29778e38974c766ad439fe21dde5/yyaac/obj/wonDkMOGw6XDiTHCmMOi/3180016084/bd03/be32/a7a4/eaec54cbb513d31f7e35309532cf25ff.m4a",
          cover: "https://p2.music.126.net/dLkalW4CyC6hAZQS6xXFeA==/109951163649564629.jpg?param=130y130", // 歌曲头像
          theme: "rgb(127, 218, 180)", // 播放这首歌曲时的主题色
        },
        {
          name: "独家村",
          artist: "谢安琪",
          url:
            "https://m801.music.126.net/20220306225937/363f7e76654c3054484ffa80070c4dc6/jdyyaac/070c/530e/0709/0f55d3f86cddd1c627b70cedd0434390.m4a",
          cover: "https://p2.music.126.net/Q9TN7GvYuj_4ECxrC6810Q==/2543170395481293.jpg?param=130y130",
          theme: "rgb(61, 162, 230)",
        },
        {
          name: "我本人",
          artist: "吴雨霏",
          url:
            "https://m10.music.126.net/20220310140641/26b6bfa7870d7b465e3d439b442bef6f/yyaac/obj/wonDkMOGw6XDiTHCmMOi/3036008024/6a04/c9bd/39f8/9508f0914f75526567f0167555e6f009.m4a",
          cover: "http://p1.music.126.net/-U2K8GKlASCSXK0cRre1gA==/109951163188718762.jpg?param=130y130",
          lrc: "../../static/lrc/我本人.lrc",
          theme: "#baf",
        },
        {
          name: "未晚",
          artist: "吴雨霏",
          url:
            "https://m10.music.126.net/20220311094501/5223797b868afbc345f38c55932dc79c/yyaac/obj/wonDkMOGw6XDiTHCmMOi/3046886892/5241/4f96/3807/9a28abd0fb2e9195252ad5ef06af1d13.m4a",
          cover: "http://p1.music.126.net/-U2K8GKlASCSXK0cRre1gA==/109951163188718762.jpg?param=130y130",
          lrc: "../../static/lrc/未晚.lrc",
          theme: "#baf",
        },
        {
          name: "邮差",
          artist: "王菲",
          url:
            "https://dl.stream.qqmusic.qq.com/C400003uRTUT1GvKoW.m4a?guid=1722838874&vkey=0B933D620EB4F6916A083A506685E612398AFCC171D910E9F02800CB81450AA3DD84050A353D4A7F4C2AD37F4181B47C8E83AC093773EA01&uin=2549829740&fromtag=66",
          cover: "https://y.qq.com/music/photo_new/T002R300x300M000003j0loN16WKM8_2.jpg?max_age=2592000",
          lrc: "../../static/lrc/邮差.lrc",
          theme: "#baf",
        },
      ],
      info: {
        lrcType: 3,
        fixed: false, // 不开启吸底模式
        listFolded: true, // 折叠歌曲列表
        autoplay: false, // 开启自动播放
        preload: "auto", // 自动预加载歌曲
        loop: "all", // 播放循环模式、all全部循环 one单曲循环 none只播放一次
        order: "list", //  播放模式，list列表播放, random随机播放
      },
    };
  },
  mounted() {
    // 初始化播放器
    this.initAudio();
  },
  methods: {
    initAudio() {
      // 创建一个音乐播放器实例，并挂载到DOM上，同时进行相关配置
      const ap = new APlayer({
        container: document.getElementById("aplayer"),
        audio: this.audio, // 音乐信息
        ...this.info, // 其他配置信息
      });
    },
    getHotArtices() {
      let that = this
      getHotArtices().then(data => {
        that.hotArticles = data.data
      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '最热文章加载失败!', showClose: true})
        }

      })

    },
    getNewArtices() {
      let that = this
      getNewArtices().then(data => {
        that.newArticles = data.data
      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '最新文章加载失败!', showClose: true})
        }

      })

    },
    getHotTags() {
      let that = this
      getHotTags().then(data => {
        that.hotTags = data.data
      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '最热标签加载失败!', showClose: true})
        }

      })
    },
    listArchives() {
      listArchives().then((data => {
        this.archives = data.data
      })).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '文章归档加载失败!', showClose: true})
        }
      })
    }

  },
  components: {
    'card-me': CardMe,
    'card-article': CardArticle,
    'card-tag': CardTag,
    ArticleScrollPage,
    CardArchive,
    APlayer
  }
}
</script>

<style scoped>

.el-container {
  width: 960px;
}

.el-aside {
  margin-left: 20px;
  width: 260px;
}

.el-main {
  padding: 0px;
  line-height: 16px;
}

.el-card {
  border-radius: 0;
}

.el-card:not(:first-child) {
  margin-top: 20px;
}
</style>
