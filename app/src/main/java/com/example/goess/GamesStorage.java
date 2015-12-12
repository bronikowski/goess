package com.example.goess;

import java.util.HashMap;

public class GamesStorage {

    static final int DEFAULT_GAMES_LIST_SIZE = 4;
    static final int RECENT_GAMES_LIST_SIZE = 10;


    HashMap<String, String> recentGamesByName = new HashMap<String, String>();
    HashMap<String, String> defaultGamesByName = new HashMap<String, String>();


    GamesStorage() {
        initDefault();
    }

    private void initDefault() {
        defaultGamesByName.put("古力 vs 李世ドル (2015/10/28)", GAME_1);
        defaultGamesByName.put("My test game", GAME_2);
        defaultGamesByName.put("古力 vs 許映皓 (2015/09/08)", GAME_3);
        defaultGamesByName.put("朴廷桓 vs 古力 (2015-12-01)", GAME_4);
    }

    public String getDefaultGameAt(String name) {
        return defaultGamesByName.get(name);
    }

    private static final String GAME_1 = "(;SZ[19]EV[2015年中国囲碁リーグ第18節]DT[2015/10/28]KM[7.5]" +
            "PB[古力]PW[李世ドル]GN[広西対重慶]PC[中国広西]RE[B+R];B[pd];W[dp];B[pq];W[dd];B[fc];W[cf];" +
            "B[qk];W[nc];B[lc];W[qc];B[qd];W[pc];B[od];W[nb];B[me];W[mp];B[po];W[jp];B[cn];W[fq];B[b" +
            "p];W[oj];B[ok];W[nk];B[nj];W[pk];B[ol];W[pj];B[pl];W[ni];B[mj];W[qj];B[mi];W[ql];B[qm];" +
            "W[rk];B[rl];W[nh];B[mh];W[ng];B[qg];W[rm];B[qk];W[mg];B[ql];W[kg];B[ph];W[pi];B[ke];W[l" +
            "h];B[mk];W[ig];B[ip];W[io];B[hp];W[jq];B[ho];W[dn];B[in];W[jo];B[cm];W[dm];B[dl];W[el];" +
            "B[dk];W[ek];B[do];W[eo];B[co];W[en];B[fm];W[dj];B[cj];W[ci];B[bk];W[bj];B[ck];W[ej];B[h" +
            "m];W[hr];B[gr];W[gq];B[ir];W[fr];B[iq];W[jn];B[jm];W[jr];B[js];W[ks];B[is];W[lr];B[km];" +
            "W[gn];B[hn];W[il];B[ij];W[hk];B[im];W[kj];B[nq];W[mn];B[kn];W[ko];B[ln];W[lo];B[no];W[n" +
            "p];B[op];W[id];B[nr];W[lq];B[mo];W[mq];B[rc];W[rb];B[rd];W[jc];B[db];W[cc];B[cb];W[gb];" +
            "B[bc];W[bd];B[dc];W[cd];B[gc];W[hb];B[ac];W[of];B[oe];W[fb];B[lf];W[ri];B[ed];W[lb];B[s" +
            "k];W[lg];B[pf];W[eb];B[ec];W[da];B[df];W[ba];B[bb];W[ca];B[cg];W[de];B[ef];W[bg];B[bf];" +
            "W[be];B[ce];W[ll];B[ml];W[cf];B[ea];W[fa];B[ce];W[rg];B[cf];W[qh];B[rf];W[qe];B[sg];W[r" +
            "h];B[re];W[sc];B[ie];W[ld];B[jd];W[kd];B[je];W[nd];B[rj];W[si];B[ic];W[ib];B[hd];W[md];" +
            "B[ne];W[le];B[kf];W[mf];B[hh];W[jk];B[jh];W[ji];B[ii];W[ih];B[sd];W[sf];B[qb];W[pb];B[s" +
            "b];W[qa];B[jj])";

    private static final String GAME_2 = "(;GM[1]FF[4]CA[UTF-8]AP[CGoban:3]ST[2]" +
            "RU[Japanese]SZ[19]KM[0.00]PW[White]PB[Black]" +
            "(;B[ji](;W[jh];B[nf](;W[ii];B[oh];W[ki];B[qk];W[jj];B[ij];W[ne];B[jk]" +
            ";W[of];B[kj];W[og];B[ji];W[mf];B[ng];W[nh];B[ph];W[mg];B[oi];W[ni]" +
            ";B[oj];W[nj];B[pj];W[ok];B[lk];W[qg];B[nn];W[qh];B[ml];W[pg];B[mm];W[pk]" +
            ";B[jn];W[qj];B[jl];W[pi])(;W[ng];B[nh];W[og];B[mg];W[ki];B[of];W[ii]" +
            ";B[pg];W[mh];B[jj];W[ij];B[oh]))(;W[jj]))(;B[fm];W[mn];B[mk];W[ql];B[qo]))";

    private static final String GAME_3 = "(;EV[第20回三星火災杯世界囲碁マスターズ本戦1回戦第1戦]DT[2015/09/08]" +
            "KM[6.5]PB[古力]PW[許映皓]PC[中国北京珠三角JWマリオットホテル]RE[W+R];B[pd];W[dp];B[pp];W[dd];B[fq];" +
            "W[cn];B[fc];W[hc];B[nc];W[fd];B[pj];W[nq];B[lq];W[no];B[pn];W[kp];B[oq];W[nr];B[kq];W[jp];B[lp];" +
            "W[lo];B[mp];W[qq];B[pq];W[qo];B[po];W[qp];B[qn];W[rn];B[rm];W[ro];B[qr];W[rr];B[mo];W[ln];B[mn];" +
            "W[lm];B[pr];W[rl];B[qm];W[rs];B[fo];W[eq];B[fr];W[fm];B[hn];W[hm];B[im];W[in];B[ho];W[il];B[jm];" +
            "W[jl];B[gm];W[hl];B[gl];W[io];B[do];W[co];B[cq];W[dl];B[cc];W[dc];B[cd];W[ce];B[be];W[cb];B[bb];" +
            "W[bd];B[bc];W[bf];B[db];W[eb];B[ca];W[fb];B[fl];W[rd];B[qd];W[re];B[rc];W[qg];B[qi];W[og];B[kc];" +
            "W[ej];B[cf];W[de];B[ad];W[bg];B[dq];W[eo];B[en];W[dn];B[fp];W[jq];B[eh];W[gi];B[dj];W[dk];B[gh];" +
            "W[hh];B[fj];W[fi];B[ei];W[ek];B[fh];W[hi];B[ch];W[cg];B[gf];W[cj];B[hg];W[rb];B[qc];W[jd];B[kd];" +
            "W[ke];B[gd];W[fe];B[je];W[ge];B[jf];W[jc];B[le];W[kb];B[lb];W[kf];B[he];W[lf];B[hd];W[gc];B[jb];" +
            "W[ld];B[lc];W[ib];B[ih];W[kh];B[ii];W[gk];B[md];W[ja];B[ki];W[li];B[kj];W[lj];B[lk];W[mk];B[kl];" +
            "W[km];B[mj];W[nj];B[mi];W[lh];B[ni];W[jk];B[kk];W[jj];B[ji];W[oi];B[nh];W[ng];B[oj];W[jg];B[ig];" +
            "W[ml];B[ij];W[hk];B[jh];W[fg];B[gg];W[eg];B[qf];W[rf];B[rh];W[rg];B[nk];W[pg];B[bh];W[ci];B[dh];" +
            "W[mg];B[fk];W[hj];B[bk];W[bj];B[aj];W[bl];B[mf];W[if];B[bi];W[ck];B[ie];W[lg])";

    private static final String GAME_4 = "(;US[棋聖道場]WR[九段]KM[6.5]DT[2015-12-01]EV[第17回農心辛拉面杯三国囲棋擂" +
            "臺戦第9局]PC[韓国釜山]PB[朴廷桓]SO[]RE[白両目半勝]BR[九段]PW[古力]SZ[19];B[pd];W[dd];B[qp];W[dq];B[oq];" +
            "W[co];B[nc];W[fc];B[pj];W[jq];B[ci];W[ck];B[bd];W[cf];B[bf];W[cg];B[bg];W[ch];B[bh];W[di];B[cj];" +
            "W[dj];B[bk];W[bl];B[jd];W[jo];B[il];W[re];B[qd];W[qh];B[qi];W[ph];B[ni];W[pf];B[nf];W[oj];B[oi];" +
            "W[pi];B[qj];W[ok];B[ql];W[lk];B[og];W[ri];B[rj];W[rg];B[si];W[sh];B[rh];W[rd];B[rc];W[ri];B[pg];" +
            "W[qg];B[rh];W[mg];B[ng];W[ri];B[sj];W[ij];B[eq];W[ep];B[fp];W[fq];B[er];W[fr];B[gp];W[hq];B[gk];" +
            "W[ak];B[km];W[in];B[dk];W[bj];B[bi];W[dl];B[ek];W[el];B[jj];W[fk];B[ii];W[ln];B[ce];W[de];B[cc];" +
            "W[sf];B[qf];W[qe];B[pe];W[sd];B[rh];W[dc];B[cb];W[ri];B[ej];W[fj];B[ei];W[dh];B[fi];W[gj];B[eg];" +
            "W[ef];B[eh];W[df];B[hh];W[hj];B[kk];W[hc];B[hm];W[gh];B[gg];W[fg];B[fh];W[im];B[ff];W[jl];B[ic];" +
            "W[ib];B[jb];W[id];B[jc];W[fe];B[gd];W[hf];B[hd];W[hb];B[fd];W[gf];B[gi];W[ie];B[ge];W[fg];B[fl];" +
            "W[jk];B[ff];W[cd];B[be];W[fg];B[cm];W[cl];B[ff];W[bb];B[bc];W[fg];B[ik];W[kj];B[ji];W[hl];B[ff];" +
            "W[ee];B[if];W[jf];B[ig];W[je];B[he];W[fg];B[kl];W[hk];B[ff];W[kd];B[ke];W[kf];B[hg];W[lc];B[ec];" +
            "W[fb];B[eb];W[rb];B[qc];W[rh];B[mm];W[lm];B[ll];W[ml];B[kn];W[pm];B[nl];W[qm];B[pl];W[nm];B[ol];" +
            "W[mn];B[mk];W[mq];B[mp];W[np];B[nq];W[lp];B[mr];W[mo];B[om];W[pp];B[po];W[op];B[pq];W[qo];B[ro];" +
            "W[qq];B[qn];W[qr];B[lq];W[rp];B[qo];W[mp];B[pr];W[os];B[rr];W[rq];B[ps];W[rs];B[ns];W[sr];B[fa];" +
            "W[kb];B[kr];W[on];B[ir];W[hr];B[pn];W[jr];B[mb];W[sc];B[ha];W[ia];B[gb];W[kc];B[gc];W[ja];B[qb];" +
            "W[rm];B[oo];W[nn];B[rl];W[lh];B[li];W[kh];B[js];W[iq];B[ki];W[is];B[md];W[me];B[mf];W[le];B[ko];" +
            "W[kp];B[mh];W[ks];B[ls];W[ne];B[nd];W[so];B[sm];W[oe];B[of];W[la];B[lf];W[lg];B[ld];W[lb];B[od];" +
            "W[ke];B[jh];W[kg];B[ma];W[oh];B[nh];W[mc];B[nb];W[kq];B[lr];W[mm];B[ai];W[aj];B[pf];W[rf];B[ra];" +
            "W[sb];B[ga];W[js];B[lj];W[jn];B[sn];W[sp];B[qa];W[jm];B[hi];W[no];B[rn];W[lo];B[qs];W[jg];B[sa])";
}
